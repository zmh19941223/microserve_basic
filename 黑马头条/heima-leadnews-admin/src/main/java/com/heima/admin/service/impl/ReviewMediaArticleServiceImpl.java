package com.heima.admin.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heima.admin.service.ReviewMediaArticleService;
import com.heima.common.aliyun.AliyunImageScanRequest;
import com.heima.common.aliyun.AliyunTextScanRequest;
import com.heima.common.common.contants.ESIndexConstants;
import com.heima.common.common.pojo.EsIndexEntity;
import com.heima.common.kafka.KafkaSender;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.mappers.admin.AdChannelMapper;
import com.heima.model.mappers.app.*;
import com.heima.model.mappers.wemedia.WmNewsMapper;
import com.heima.model.mappers.wemedia.WmUserMapper;
import com.heima.model.media.pojos.WmNews;
import com.heima.model.media.pojos.WmUser;
import com.heima.model.mess.admin.ArticleAuditSuccess;
import com.heima.model.user.pojos.ApUserMessage;
import com.heima.utils.common.Compute;
import com.heima.utils.common.ZipUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Log4j2
@SuppressWarnings("all")
public class ReviewMediaArticleServiceImpl implements ReviewMediaArticleService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private AliyunTextScanRequest aliyunTextScanRequest;

    @Autowired
    private AliyunImageScanRequest aliyunImageScanRequest;

    @Override
    public void autoReviewArticleByMedia(Integer newsId) {
        //根据文章id查询文章信息
        WmNews wmNews = wmNewsMapper.selectNewsDetailByPrimaryKey(newsId);

        if(wmNews!=null){
            //状态为4的时候，直接保存数据
            if(wmNews.getStatus()==4){
                reviewSuccessSaveAll(wmNews);
                return;
            }
            //审核通过后待发布文章，判断发布时间
            if(wmNews.getStatus()==8 && wmNews.getPublishTime()!=null && wmNews.getPublishTime().getTime()<new Date().getTime()){
                reviewSuccessSaveAll(wmNews);
                return;
            }
            //审核文章
            if(wmNews.getStatus()==1){
                //审核文章的标题与内容的匹配度
                String content = wmNews.getContent();
                String title = wmNews.getTitle();
                double degree = Compute.SimilarDegree(content, title);
                if(degree<=0){
                    //文章标题与内容匹配
                    updateWmNews(wmNews,(short)2,"文章标题与内容不匹配");
                    return;
                }
                //审核文本内容 阿里接口
                List<String> images = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                JSONArray jsonArray = JSON.parseArray(content);
                handlerTextAndImages(images,sb,jsonArray);

                try {
                    /*String response = aliyunTextScanRequest.textScanRequest(sb.toString());
                    if("review".equals(response)){//人工审核
                        updateWmNews(wmNews,(short)3,"需要人工审核");
                        return;
                    }
                    if("block".equals(response)){//审核失败
                        updateWmNews(wmNews,(short)2,"文本内容审核失败");
                        return;
                    }*/
                    //审核文章中的图片信息，阿里接口
                    /*String imagesResponse = aliyunImageScanRequest.imageScanRequest(images);
                    if(imagesResponse!=null){
                        if("review".equals(imagesResponse)){//人工审核
                            updateWmNews(wmNews,(short)3,"需要人工审核");
                            return;
                        }
                        if("block".equals(imagesResponse)){//审核失败
                            updateWmNews(wmNews,(short)2,"图片内容审核失败");
                            return;
                        }
                    }else{
                        updateWmNews(wmNews,(short)2,"图片审核出现问题");
                        return;
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if(wmNews.getPublishTime()!=null){
                    if(wmNews.getPublishTime().getTime()>new Date().getTime()){
                        //修改wmnews的状态为8
                        updateWmNews(wmNews,(short)8,"待发布");
                    }else{
                        //立即发布
                        reviewSuccessSaveAll(wmNews);
                    }
                }else{
                    //立即发布
                    reviewSuccessSaveAll(wmNews);
                }
            }
        }

    }

    /**
     * 找出文本内容和图片列表
     * @param images
     * @param sb
     * @param jsonArray
     */
    private void handlerTextAndImages(List<String> images, StringBuilder sb, JSONArray jsonArray) {
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            String type = (String) jsonObj.get("type");
            if("image".equals(type)){
                String value = (String) jsonObj.get("value");
                images.add(value);
            }
            if("text".equals(type)){
                sb.append(jsonObj.get("value"));
            }
        }
    }

    /**
     * 修改wmnews
     * @param wmNews
     * @param i
     * @param 待发布
     */
    private void updateWmNews(WmNews wmNews, short status, String message) {
        wmNews.setStatus(status);
        wmNews.setReason(message);
        wmNewsMapper.updateByPrimaryKeySelective(wmNews);
    }

    @Autowired
    private ApAuthorMapper apAuthorMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private AdChannelMapper adChannelMapper;

    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private JestClient jestClient;

    @Autowired
    private ApUserMessageMapper apUserMessageMapper;

    @Autowired
    private KafkaSender kafkaSender;


    /**
     * 保存数据
     * ap_article_config
     * ap_article
     * ap_article_content
     * ap_author
     * @param wmNews
     */
    private void reviewSuccessSaveAll(WmNews wmNews) {

        ApAuthor apAuthor = null;
        if(wmNews.getUserId()!=null){
            WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
            if(wmUser!=null && wmUser.getName()!=null){
                //查询或ap_author
                apAuthor = apAuthorMapper.selectByAuthorName(wmUser.getName());
                if(apAuthor==null || apAuthor.getId()==null){
                    apAuthor = new ApAuthor();
                    apAuthor.setUserId(wmNews.getUserId());
                    apAuthor.setCreatedTime(new Date());
                    apAuthor.setType(2);
                    apAuthor.setName(wmUser.getName());
                    apAuthor.setWmUserId(wmUser.getId());
                    apAuthorMapper.insert(apAuthor);
                }
            }
        }

        //ap_article


        ApArticle apArticle = new ApArticle();
        if(apAuthor!=null){
            apArticle.setAuthorId(apAuthor.getId().longValue());
            apArticle.setAuthorName(apAuthor.getName());
        }
        apArticle.setCreatedTime(new Date());
        Integer channelId = wmNews.getChannelId();
        if(channelId!=null){
            AdChannel adChannel = adChannelMapper.selectByPrimaryKey(channelId);
            apArticle.setChannelId(channelId);
            apArticle.setChannelName(adChannel.getName());
        }
        apArticle.setLayout(wmNews.getType());
        apArticle.setTitle(wmNews.getTitle());
        String images = wmNews.getImages();//访问路径  serverurl+文件id
        if(images!=null){
            String[] split = images.split(",");
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i<split.length;i++){
                if(i>0){
                    sb.append(",");
                }
                sb.append(fileServerUrl);
                sb.append(split[i]);
            }
            apArticle.setImages(sb.toString());
        }
        apArticleMapper.insert(apArticle);
        //ap_article_config,ap_article_content
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(ZipUtils.gzip(wmNews.getContent()));
        apArticleContentMapper.insert(apArticleContent);

        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setArticleId(apArticle.getId());
        apArticleConfig.setIsComment(true);
        apArticleConfig.setIsDelete(false);
        apArticleConfig.setIsDown(false);
        apArticleConfig.setIsForward(true);
        apArticleConfigMapper.insert(apArticleConfig);

        //创建es索引
        EsIndexEntity esIndexEntity = new EsIndexEntity();
        esIndexEntity.setId(apArticle.getId().longValue());
        esIndexEntity.setChannelId(new Long(channelId));
        esIndexEntity.setContent(wmNews.getContent());
        esIndexEntity.setPublishTime(new Date());
        esIndexEntity.setStatus(new Long(1));
        esIndexEntity.setTitle(wmNews.getTitle());
        if(wmNews.getUserId()!=null){
            esIndexEntity.setUserId(wmNews.getUserId());
        }
        esIndexEntity.setTitle("media");

        Index.Builder builder = new Index.Builder(esIndexEntity);
        builder.id(apArticle.getId().toString());
        builder.refresh(true);
        Index build = builder.index(ESIndexConstants.ARTICLE_INDEX).type(ESIndexConstants.DEFAULT_DOC).build();
        JestResult result = null;
        try {
            result = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("执行ES创建索引失败，message:{}",e.getMessage());
        }
        if(result!=null && !result.isSucceeded()){
            //打印日志信息
            log.error("插入更新索引失败：message:{}",result.getErrorMessage());
        }
        //修改wmNews的状态 为  9
        wmNews.setArticleId(apArticle.getId());
        updateWmNews(wmNews,(short)9,"审核成功");
        ArticleAuditSuccess articleAuditSuccess = new ArticleAuditSuccess();
        articleAuditSuccess.setArticleId(apArticle.getId());
        articleAuditSuccess.setType(ArticleAuditSuccess.ArticleType.WEMEDIA);
        kafkaSender.sendArticleAuditSuccessMessage(articleAuditSuccess);

        //通知用户审核成功
        ApUserMessage apUserMessage = new ApUserMessage();
        apUserMessage.setUserId(wmNews.getUserId());
        apUserMessage.setCreatedTime(new Date());
        apUserMessage.setIsRead(false);
        apUserMessage.setContent("文章审核成功");
        apUserMessage.setType(108);//文章审核通过
        apUserMessageMapper.insertSelective(apUserMessage);

    }
}

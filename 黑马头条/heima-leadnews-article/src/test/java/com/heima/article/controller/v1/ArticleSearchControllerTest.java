package com.heima.article.controller.v1;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.model.article.dtos.UserSearchDto;
import com.heima.model.user.pojos.ApUserSearch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ArticleSearchControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void testLoadArticleInfo() throws Exception {
        UserSearchDto dto = new UserSearchDto();
        dto.setEquipmentId(1);
        dto.setPageSize(20);
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/article/search/load_search_history")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(dto));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDelUserSearch() throws Exception {
        UserSearchDto dto = new UserSearchDto();
        dto.setEquipmentId(1);
        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setId(8103);
        List<ApUserSearch> list = new ArrayList<>();
        list.add(apUserSearch);
        dto.setHisList(list);
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/article/search/del_search")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(dto));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testClearUserSearch() throws Exception {
        UserSearchDto dto = new UserSearchDto();
        dto.setEquipmentId(1);
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/article/search/clear_search")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(dto));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testHotkeywords() throws Exception {
        UserSearchDto dto = new UserSearchDto();
//        dto.setHotDate("2019-07-24");
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/article/search/load_hot_keywords")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(dto));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testSearchassociate() throws Exception {
        UserSearchDto dto = new UserSearchDto();
        dto.setPageSize(20);
        dto.setSearchWords("黑马");
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/article/search/associate_search")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(dto));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testEsArticleSearch() throws Exception {
        UserSearchDto dto = new UserSearchDto();
        dto.setEquipmentId(1);
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setSearchWords("面试");
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/article/search/article_search")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(dto));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

}

package com.itheima.prize.commons.db.entity;

import java.io.Serializable;
import java.util.Date;

public class CardUserGame implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column card_user_game.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column card_user_game.userid
     *
     * @mbg.generated
     */
    private Integer userid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column card_user_game.gameid
     *
     * @mbg.generated
     */
    private Integer gameid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column card_user_game.createtime
     *
     * @mbg.generated
     */
    private Date createtime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table card_user_game
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column card_user_game.id
     *
     * @return the value of card_user_game.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column card_user_game.id
     *
     * @param id the value for card_user_game.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column card_user_game.userid
     *
     * @return the value of card_user_game.userid
     *
     * @mbg.generated
     */
    public Integer getUserid() {
        return userid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column card_user_game.userid
     *
     * @param userid the value for card_user_game.userid
     *
     * @mbg.generated
     */
    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column card_user_game.gameid
     *
     * @return the value of card_user_game.gameid
     *
     * @mbg.generated
     */
    public Integer getGameid() {
        return gameid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column card_user_game.gameid
     *
     * @param gameid the value for card_user_game.gameid
     *
     * @mbg.generated
     */
    public void setGameid(Integer gameid) {
        this.gameid = gameid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column card_user_game.createtime
     *
     * @return the value of card_user_game.createtime
     *
     * @mbg.generated
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column card_user_game.createtime
     *
     * @param createtime the value for card_user_game.createtime
     *
     * @mbg.generated
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
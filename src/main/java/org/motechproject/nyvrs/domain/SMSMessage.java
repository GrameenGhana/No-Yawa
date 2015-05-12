/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.motechproject.nyvrs.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

/**
 *
 * @author seth
 */
@Entity
public class Message extends MdsEntity{
    
    @Field(name = "campaignid")
    private String campaignId;
    @Field(name = "week")
    private Integer week;
    @Field(name = "nyday")
    private String nyday;

    @Field(name = "msg")
    private String msg;

    public Message(String campaignId, Integer week, String nyday, String msg) {
        this.campaignId = campaignId;
        this.week = week;
        this.nyday = nyday;
        this.msg = msg;
    }

    
    
    /**
     * @return the campaignId
     */
    public String getCampaignId() {
        return campaignId;
    }

    /**
     * @param campaignId the campaignId to set
     */
    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    /**
     * @return the week
     */
    public Integer getWeek() {
        return week;
    }

    /**
     * @param week the week to set
     */
    public void setWeek(Integer week) {
        this.week = week;
    }

    /**
     * @return the nyday
     */
    public String getNyday() {
        return nyday;
    }

    /**
     * @param nyday the nyday to set
     */
    public void setNyday(String nyday) {
        this.nyday = nyday;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    
}

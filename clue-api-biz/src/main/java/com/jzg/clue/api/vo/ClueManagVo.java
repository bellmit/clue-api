package com.jzg.clue.api.vo;

import com.jzg.framework.core.vo.BaseVo;

import java.math.BigDecimal;

/**
 * Created by JZG on 2017/4/21.
 */
public class ClueManagVo implements BaseVo{

    private String Sign;
    /**
     * 车型名称【必填】
     */
    private String StyleName;
    /**
     * 上牌城市[必填]
     */
    private String CityName;
    /**
     * 上牌时间[必填 如：2016年1月]
     */
    private String RegDate;
    /**
     * 行驶里程(单位：万公里) [必填]
     */
    private BigDecimal Mileage;
    /**
     * 线索类型 [必填](三种：买车，卖车，金融)
     */
    private String ClueType;
    /**
     *联系人[必填]
     */
    private String ContactsName;
    /**
     *联系电话
     */
    private String ContactsPhone;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道Id
     */
    private String channelId;

    /**
     * 线索id
     */
    private Long clueId;

    /**
     * usercode
     */
    private String userCode;

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }

    public String getStyleName() {
        return StyleName;
    }

    public void setStyleName(String styleName) {
        StyleName = styleName;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getRegDate() {
        return RegDate;
    }

    public void setRegDate(String regDate) {
        RegDate = regDate;
    }

    public BigDecimal getMileage() {
        return Mileage;
    }

    public void setMileage(BigDecimal mileage) {
        Mileage = mileage;
    }

    public String getClueType() {
        return ClueType;
    }

    public void setClueType(String clueType) {
        ClueType = clueType;
    }

    public String getContactsName() {
        return ContactsName;
    }

    public void setContactsName(String contactsName) {
        ContactsName = contactsName;
    }

    public String getContactsPhone() {
        return ContactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        ContactsPhone = contactsPhone;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Long getClueId() {
        return clueId;
    }

    public void setClueId(Long clueId) {
        this.clueId = clueId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}

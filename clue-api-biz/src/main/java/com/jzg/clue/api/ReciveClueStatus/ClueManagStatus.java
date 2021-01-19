package com.jzg.clue.api.ReciveClueStatus;

/**
 * Created by JZG on 2017/4/21.
 */
public enum ClueManagStatus {
    /**
     * 推送成功
     */
    Ok(100, "线索推送成功"),
    /**
     * 线索已存在
     */
    Repeat(703, "线索已存在"),
    /**
     * 异常
     */
    Exception(500, "异常"),
    /**
     * 没有权限
     */
    NoAuth(701, "没有权限"),
    /**
     * 车型名称为空
     */
    StyleNameIsNull(702, "车型名称为空"),

    /**
     * 上牌城市为空
     */
    CityNameIsNull(702, "上牌城市为空"),
    /**
     * 上牌时间为空
     */
    RegDateIsNull(702, "上牌时间为空"),
    /**
     * 行驶里程为空
     */
    MileageIsNull(702, "行驶里程为空"),
    /**
     * 线索类型为空
     */
    ClueTypeIsNull(702, "线索类型为空"),
    /**
     * 联系人为空
     */
    ContactsNameIsNull(702, "联系人为空");


    private int value;
    private String text;

    /**
     * 构造方法
     *
     * @param value 状态吗
     * @param text  文字说明
     */
    private ClueManagStatus(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获取value
     *
     * @return 状态码
     */
    public int getValue() {
        return this.value;
    }

    /**
     * 获取说明
     *
     * @return 文字说明
     */
    public String getText() {
        return this.text;
    }
}

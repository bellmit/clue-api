package com.jzg.clue.api.util;


/**
 *
 * @author mush
 * @time  2017/6/6 19:09
 * @describe 接口返回值和返回信息的枚举类
 */
public enum ResultMsg {

    Result_Push_Success(100,"线索推送成功！"),
    Result_Push_Failed(101,"线索推送失败！"),
    Result_Push_Repeat(102,"推送线索重复！"),
    Result_Channel_AuthCode_Error(103,"渠道授权码错误!"),
    Result_Channel_AuthCode_NotNull(104,"渠道授权码不能为空!"),
    Result_Parameter_IsNull(105,"调用接口参数为空,请传入线索参数信息(约定JSON字符串)!"),
    Result_Failed_Check_Parameter(106,"失败,请检查是否有必填的参数未传值!"),
    Result_Failed_Convert_Exception(107,"失败,转化对象异常,请检查参数!"),
    Result_Push_CityName_Invalid(108,"推送的城市不在经营范围!"),
    Result_Km_NotNull(109,"公里数不能为空!"),
    Result_Vehicle_Type_NotNull(110,"车型不能为空!"),
    Result_CityName_NotNull(111,"城市名不能为空!"),
    Result_Vehicle_Register_Date_NotNull(112,"上牌日期不能为空!"),
    Result_Clue_Type_NotNull(113,"线索类型不能为空!"),
    Result_Contact_Person_NotNull(114,"联系人不能为空!"),
    Result_Push_Brand_Invalid(115,"推送的品牌不在经营范围!"),
    Result_Clue_Type_Invalid(116,"推送的线索类型无效!"),
    Result_Contact_Phone_NotNull(117,"联系电话不能为空!"),
    Result_Contact_Phone_Repeat(118,"联系电话已存在!"),
    //针对爱卡返回成功状态
    Result_AK_Success(200,"该合作线索入库成功！"),
    Result_AK_NotExist(201,"检测您的线索id是否正确！"),
    Result_AK_Pending(202,"待处理！"),
    Result_AK_NotCity(203,"非业务合作城市！"),
    Result_AK_WaitingStorage(204,"数据正在等待入库，请稍后查询！"),
    Result_AK_Repeat(205,"库中已存在这条线索， 该合作线索不生效！"),
    Result_AK_Malformed_Phone(206,"手机号格式错误！"),
    /**
     * 查询
     */
    Result_Success(200,"成功"),
    Result_Channel_Send_IsNull(201,"该渠道在一小时之内没获取到线索数据");


    private int value;
    private String msg = "";
    private ResultMsg(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public int getValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

}

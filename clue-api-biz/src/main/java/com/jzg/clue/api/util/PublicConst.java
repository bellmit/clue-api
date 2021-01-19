package com.jzg.clue.api.util;

/**
 * Created by dougp on 2017/3/31.
 */
public class PublicConst {
    //系统级别
    public static final String BASE_ENCODING = "UTF-8";


    public static final int PRETREATMENT = 3;//预处理 未知
    public static final int STORAGEMODE = 1;//入库方式  :批量导入
    public static final int STORAGEMODE_Interface = 2;//入库方式  :接口同步
    //线索编码
    public static final String CLUECODE_BUY = "Buy-00000000000";
    public static final String CLUECODE_SELL = "Sell-00000000000";
    public static final String CLUECODE_FINANCE = "Finance-00000000000";
    //线索属性
    public static final int CLUEPROPER_BUY = 1;//买车
    public static final int CLUEPROPER_SELL = 2;//卖车
    public static final int CLUEPROPER_FINANCE = 3;//金融
    public static final String CLUEPROPER_BUY_STR = "买车";
    public static final String CLUEPROPER_SELL_STR = "卖车";
    public static final String CLUEPROPER_FINANCE_STR = "金融";

    //线索日志
    public static final int RECEIVELOG_SUCCESS = 1;//接收线索日志成功
    public static final int RECEIVELOG_fAILED = 0; //接收线索日志失败
    //渠道属性
    public static final int CHANNEL_SEND_TYPE_NOT_DIRECT = 1; //渠道发送类型：先审后发
    public static final int CHANNEL_SEND_TYPE_DIRECT = 0;     //渠道发送类型：直接发送

    public static final int PLATFORM_GUAZI = 5; //瓜子平台Id
    public static final int PLATFORM_TTPAI = 1; //天天拍平台id
    public static final int PLATFORM_GUAZI_74 = 74; //瓜子平台"所查询的线索不存在"
    public static final int PLATFORM_GUAZI_76 = 76; //瓜子平台"根据条件并未找到该合作渠道推送的线索"
    public static final int PLATFORM_GUAZI_77 = 77; //瓜子平台"合作线索不符合过滤规则，过滤失败"
    public static final int PLATFORM_GUAZI_78 = 78; //瓜子平台"瓜子库中已存在这条线索， 该合作线索不生效"
    public static final String RESULT_MESSAGE = "手机号已经报名"; //天天拍返回手机号重复
    public static final String RESULT_MESSAGE1 = "手机号重复"; //天天拍返回手机号重复
    public static final long GUAZI_MIN = 50; //爱卡查询状态瓜子推迟50分钟
    public static final int PLATFORM_TYPE = 2;
}

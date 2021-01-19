package com.jzg.clue.api.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jzg.clue.api.util.PublicConst;
import com.jzg.clue.api.util.ResultMsg;
import com.jzg.clue.api.vo.CarEstimateResVo;
import com.jzg.clue.api.vo.ClueManagVo;
import com.jzg.clue.api.vo.ClueStatusDao;
import com.jzg.clue.service.api.*;
import com.jzg.clue.service.dto.ReturnInfoDto;
import com.jzg.clue.service.dto.SalesCluePushMQModel;
import com.jzg.clue.service.model.*;
import com.jzg.framework.cache.redis.RedisCache;
import com.jzg.framework.core.vo.ResultListVo;
import com.jzg.framework.core.vo.ResultVo;
import com.jzg.framework.core.vo.RetStatus;
import com.jzg.framework.utils.encrypt.Md5Encrypt;
import com.jzg.framework.utils.string.StringUtils;
import com.jzg.framework.utils.web.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JZG on 2017/4/21.
 */
@Component("reciveClueManagBiz")
public class ReciveClueManagBiz {
    SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReciveClueManagBiz.class);

    /***
     * userChannelService 声明
     */
    @Resource
    private UserChannelService userChannelService;

    /***
     * userChannelService 声明
     */
    @Resource
    private ReceiveLogService receiveLogService;

    /***
     * cluePushMqBiz 声明
     */
    @Resource
    private CluePushMqBiz cluePushMqBiz;

    /***
     * clueManagService 声明
     */
    @Resource
    private ClueManagService clueManagService;

    /***
     * clueStatusService 声明
     */
    @Resource
    private ClueStatusService clueStatusService;

    /***
     * 获取log记录
     */
    @Resource
    private PushLogService pushLogService;

    /**
     * 垃圾号码段记录表
     */
    @Resource
    private ClueRubbishNumberService clueRubbishNumberService;

    /**
     * 报警短信记录服务
     */
    @Resource
    private ClueSmsEmailService clueSmsEmailService;
    /**
     * redis 缓存
     */
    @Resource
    private RedisCache redisCache;

    /*
     *爱卡覆盖城市
     */
    @Value("${aika.CoveringCityList}")
    private String aikaCovingCityList;

    /*
     *微车覆盖城市
     */
    @Value("${weiche.CoveringCityList}")
    private String weicheCovingCityList;
    /***
     * 可以调用获取状态的渠道id
     */
    @Value("${interface.status.NoAuthchannelId}")
    private String getStatusNoAuthchannelIdList;

    /*
     * 垃圾号码段次数
     */
    @Value("${rubbish.number}")
    private String rubbish_number;
    /*
     * 报警内容
     */
    @Value("${monitor.rubbish.number}")
    private String monitor_content;
    /*
     * 二手车之家key
     */
    @Value("${CarEstimate.key}")
    private String CarEstimate_key;
    /*
     * 获取二手车之家线索
     */
    @Value("${CarEstimate.queryURL}")
    private String CarEstimate_queryURL;
    /*
     * 二手之家线索回传状态
     */
    @Value("${CarEstimate.updateStateURL}")
    private String CarEstimate_updateStateURL;
    /**
     * 渠道标识
     */
    @Value("${CarEstimate.sign}")
    private String CarEstimate_sign;
    /*
     *二手车之家覆盖城市
     */
    @Value("${CarEstimate.city}")
    private String CarEstimate_city;


    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat format3 = new SimpleDateFormat("yyyy.MM");
    SimpleDateFormat format4 = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat format5 = new SimpleDateFormat("yyyy/MM");

    /**
     * 收集线索接口 到MQ
     *
     * @param ClusData1
     * @param request
     * @return
     */

    public ResultVo<Long> insertReceiveClueManagToMQ(String ClusData1, HttpServletRequest request) {

        ResultVo resultVo = new ResultVo();
        if (ClusData1 != null) {
            try {
                ClueManagVo ClusData = JSON.toJavaObject(JSON.parseObject(ClusData1), ClueManagVo.class);
                try {
                    if (ClusData != null) {
                        if (!"".equals(ClusData.getSign().trim()) && ClusData.getSign() != null) {
                            if (StringUtils.isNotBlank(ClusData.getChannelId()) && StringUtils.isNotBlank(ClusData.getChannelCode())) {
                                int intClueProper = 0;
                                String strClueProper = "";
                                String strClueCode = "";

                                ClueManag clueManag = new ClueManag();
                                clueManag.setChannelid(Integer.valueOf(ClusData.getChannelId()));
                                ReceiveLog receiveLog = new ReceiveLog();
                                receiveLog.setChannelcode(ClusData.getChannelCode());
                                receiveLog.setChannelname("");
                                receiveLog.setCreatetime(new Date());
                                receiveLog.setPushcontent(ClusData1);
                                try {
                                    StringBuffer s = request.getRequestURL();
                                    String PushUrl = s.toString();
                                    receiveLog.setPushurl(PushUrl);
                                } catch (Exception e) {
                                    String PushUrl = "";
                                    receiveLog.setPushurl(PushUrl);
                                }
                                if (!"".equals(ClusData.getMileage()) && ClusData.getMileage() != null) {
                                    BigDecimal bigMiileage = new BigDecimal(ClusData.getMileage().toString());
                                    bigMiileage.setScale(2, BigDecimal.ROUND_HALF_UP);
                                    clueManag.setMiileage(bigMiileage);
                                } else {
                                    resultVo.setStatus(ResultMsg.Result_Km_NotNull.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Km_NotNull.getMsg());
                                    return resultVo;
                                }
                                if (!"".equals(ClusData.getStyleName().trim()) && ClusData.getStyleName().trim() != null) {
                                    clueManag.setStylename(ClusData.getStyleName().trim());
                                } else {
                                    resultVo.setStatus(ResultMsg.Result_Vehicle_Type_NotNull.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Vehicle_Type_NotNull.getMsg());
                                    return resultVo;
                                }
                                if (!"".equals(ClusData.getCityName().trim()) && ClusData.getCityName().trim() != null) {
                                    clueManag.setCityname(ClusData.getCityName().trim());
                                } else {
                                    resultVo.setStatus(ResultMsg.Result_CityName_NotNull.getValue());
                                    resultVo.setMsg(ResultMsg.Result_CityName_NotNull.getMsg());
                                    return resultVo;
                                }
                                if (!"".equals(ClusData.getRegDate().trim()) && ClusData.getRegDate().trim() != null) {
                                    clueManag.setRegdate(getRegDate(ClusData.getRegDate().trim()));
                                } else {
                                    resultVo.setStatus(ResultMsg.Result_Vehicle_Register_Date_NotNull.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Vehicle_Register_Date_NotNull.getMsg());
                                    return resultVo;
                                }
                                if (!"".equals(ClusData.getClueType().trim()) && ClusData.getClueType().trim() != null) {
                                    if (ClusData.getClueType().trim().equals(PublicConst.CLUEPROPER_BUY_STR)) {//买车
                                        intClueProper = PublicConst.CLUEPROPER_BUY;
                                        clueManag.setCluecode(ClusData.getChannelCode() + "-" + "Buy");
                                    } else if (ClusData.getClueType().trim().equals(PublicConst.CLUEPROPER_SELL_STR)) {//买车
                                        intClueProper = PublicConst.CLUEPROPER_SELL;
                                        clueManag.setCluecode(ClusData.getChannelCode() + "-" + "Sell");
                                    } else if (ClusData.getClueType().trim().equals(PublicConst.CLUEPROPER_FINANCE_STR)) {//金融
                                        intClueProper = PublicConst.CLUEPROPER_FINANCE;
                                        clueManag.setCluecode(ClusData.getChannelCode() + "-" + "Finance");
                                    }
                                } else {
                                    resultVo.setStatus(ResultMsg.Result_Clue_Type_NotNull.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Clue_Type_NotNull.getMsg());
                                    return resultVo;
                                }
                                if (!"".equals(ClusData.getContactsName().trim()) && ClusData.getContactsName().trim() != null) {
                                    clueManag.setName(ClusData.getContactsName());
                                } else {
                                    resultVo.setStatus(ResultMsg.Result_Contact_Person_NotNull.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Contact_Person_NotNull.getMsg());
                                    return resultVo;
                                }
                                if (!"".equals(ClusData.getContactsPhone().trim()) && ClusData.getContactsPhone().trim() != null) {
                                    clueManag.setPhone(ClusData.getContactsPhone().trim());
                                } else {
                                    resultVo.setStatus(ResultMsg.Result_Contact_Phone_NotNull.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Contact_Phone_NotNull.getMsg());
                                    return resultVo;
                                }
                                clueManag.setClueproper(intClueProper);
                                clueManag.setUsercode("");
                                clueManag.setStoragemode(PublicConst.STORAGEMODE_Interface);
                                clueManag.setPretreatment(PublicConst.PRETREATMENT);
                                clueManag.setStoragetime(new Date());
                                clueManag.setPushplatform("");
                                clueManag.setRemark("");
                                clueManag.setDisstatus(0);
                                try {
                                    //推送到队列
                                    cluePushMqBiz.PushClusToRabbitMQ(clueManag);
                                    receiveLog.setStatus(1);
                                    receiveLogService.insertReceiveLog(receiveLog);
                                    resultVo.setData("渠道Id:" + clueManag.getChannelid());
                                    resultVo.setStatus(ResultMsg.Result_Push_Success.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Push_Success.getMsg());
                                } catch (Exception e) {
                                    receiveLog.setStatus(0);
                                    receiveLogService.insertReceiveLog(receiveLog);
                                    resultVo.setStatus(ResultMsg.Result_Push_Failed.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Push_Failed.getMsg());
                                    LOGGER.error("[clue-api][ReciveClueManagBiz][insertReceiveClueManagToMQ] :收集线索接口到MQ 。" + "传入参数：ClusData1={},异常信息=", ClusData1, e);

                                }

                            } else {
                                resultVo.setStatus(ResultMsg.Result_Channel_AuthCode_Error.getValue());
                                resultVo.setMsg(ResultMsg.Result_Channel_AuthCode_Error.getMsg());
                            }
                        } else {
                            resultVo.setStatus(ResultMsg.Result_Channel_AuthCode_NotNull.getValue());
                            resultVo.setMsg(ResultMsg.Result_Channel_AuthCode_NotNull.getMsg());
                        }
                    } else {
                        resultVo.setStatus(ResultMsg.Result_Parameter_IsNull.getValue());
                        resultVo.setMsg(ResultMsg.Result_Parameter_IsNull.getMsg());
                    }
                } catch (Exception e) {
                    resultVo.setStatus(ResultMsg.Result_Failed_Check_Parameter.getValue());
                    resultVo.setMsg(ResultMsg.Result_Failed_Check_Parameter.getMsg());

                    LOGGER.error("[clue-api][ReciveClueManagBiz][insertReceiveClueManagToMQ] :收集线索接口到MQ ，请检查是否有必填的参数未传值。" + "传入参数：ClusData1={},异常信息=", ClusData1, e);

                }
            } catch (Exception e) {
                resultVo.setStatus(ResultMsg.Result_Failed_Convert_Exception.getValue());
                resultVo.setMsg(ResultMsg.Result_Failed_Convert_Exception.getMsg());
                LOGGER.error("[clue-api][ReciveClueManagBiz][insertReceiveClueManagToMQ] :收集线索接口到MQ ，转化对象异常,请检查参数。" + "传入参数：ClusData1={},异常信息=", ClusData1, e);

            }
        } else {
            resultVo.setStatus(ResultMsg.Result_Parameter_IsNull.getValue());
            resultVo.setMsg(ResultMsg.Result_Parameter_IsNull.getMsg());

        }
        return resultVo;
    }

    /**
     * 通过渠道sign取渠道信息
     *
     * @param userChannelSign 渠道guid
     * @return
     */
    public UserChannel getUserChannelByGUID(String userChannelSign) {
        UserChannel userChannel = new UserChannel();
        try {
            String key = "jzgClue_UserChannel_ByGUID_" + userChannelSign;
            String strUserChannelInfo = redisCache.get(key);
            if (strUserChannelInfo == null || strUserChannelInfo.equals("null") || strUserChannelInfo.isEmpty()) {
                ResultVo<UserChannel> resultVo = userChannelService.getUserChannelByGUID(userChannelSign);
                LOGGER.debug("[clue-api][ReciveClueManagBiz][getUserChannelByGUID] :通过signg获取渠道  getUserChannelByGUID。" + "传入参数：userChannelSign={},getUserChannelByGUID={}", userChannelSign, JSON.toJSONString(resultVo));

                if (resultVo != null && resultVo.getStatus() == RetStatus.Ok.getValue()) {
                    userChannel = resultVo.getData();
                    redisCache.set(key, 60 * 60 * 24, JSON.toJSONString(userChannel));

                } else {
                    userChannel = null;
                }
            } else {
                userChannel = JSON.parseObject(strUserChannelInfo, UserChannel.class);
            }
        } catch (Exception e) {

            LOGGER.error("[clue-api][ReciveClueManagBiz][getUserChannelByGUID] :通过signg获取渠道信息异常。" + "传入参数：userChannelSign={},异常信息=", userChannelSign, e);

        }
        return userChannel;
    }

    /**
     * 收集微车线索接口
     *
     * @param
     * @return ｛status:100, data:{}, msg:null｝
     */
    public ResultVo<Long> insertReceiveWeiCheClueManag(String ClusData, HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        try {
            if (!StringUtils.isEmpty(ClusData)) {
                ClueManagVo clueManagVo = null;
                try {
                    ClusData = URLDecoder.decode(ClusData, "UTF-8");
                    JSONObject json = (JSONObject) JSONObject.parse(ClusData);
                    clueManagVo = json.toJavaObject(ClueManagVo.class);
                } catch (Exception e) {
                    resultVo.setStatus(ResultMsg.Result_Failed_Convert_Exception.getValue());
                    resultVo.setMsg(ResultMsg.Result_Failed_Convert_Exception.getMsg());
                    return resultVo;
                }
                if (clueManagVo != null) {
                    if (!StringUtils.isEmpty(clueManagVo.getCityName())) {
                        //判断城市是否符合规则
                        Boolean IsHasCityName = weicheCovingCityList.indexOf(clueManagVo.getCityName()) >= 0;
                        if (IsHasCityName) {
                            if (!StringUtils.isEmpty(clueManagVo.getContactsPhone())) {
                                //电话90天内不能重复
                                ResultVo<Boolean> resultVo1 = clueManagService.existsPhone(0, clueManagVo.getContactsPhone(), 90);
                                if (resultVo1.getData()) {
                                    resultVo.setStatus(ResultMsg.Result_Contact_Phone_Repeat.getValue());
                                    resultVo.setMsg(ResultMsg.Result_Contact_Phone_Repeat.getMsg());
                                } else {
                                    resultVo = insertReceiveClueManag(clueManagVo, ClusData, request);
                                }
                            } else {
                                resultVo.setStatus(ResultMsg.Result_Contact_Phone_NotNull.getValue());
                                resultVo.setMsg(ResultMsg.Result_Contact_Phone_NotNull.getMsg());
                            }

                        } else {
                            resultVo.setStatus(ResultMsg.Result_Push_CityName_Invalid.getValue());
                            resultVo.setMsg(ResultMsg.Result_Push_CityName_Invalid.getMsg());
                        }

                    } else {
                        resultVo.setStatus(ResultMsg.Result_CityName_NotNull.getValue());
                        resultVo.setMsg(ResultMsg.Result_CityName_NotNull.getMsg());
                    }


                    //cheif(!StringUtils.isEmpty(clueManagVo.getCityName())&&)

                } else {
                    resultVo.setStatus(ResultMsg.Result_Failed_Convert_Exception.getValue());
                    resultVo.setMsg(ResultMsg.Result_Failed_Convert_Exception.getMsg());
                }

            } else {
                resultVo.setStatus(ResultMsg.Result_Parameter_IsNull.getValue());
                resultVo.setMsg(ResultMsg.Result_Parameter_IsNull.getMsg());
            }
        } catch (Exception e) {
            resultVo.setStatus(RetStatus.Exception.getValue());
            resultVo.setMsg("收集微车线索接口异常");
            LOGGER.error("[clue-api][ReciveClueManagBiz][insertReceiveWeiCheClueManag] :收集微车线索接口 。" + "传入参数：ClusData={},异常信息=", ClusData, e);
        }
        return resultVo;
    }

    /**
     * 收集线索接口
     *
     * @param
     * @return ｛status:100, data:{}, msg:null｝
     */
    public ResultVo<Long> insertReceiveClueManag(String ClusData1, HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        if (ClusData1 != null) {
            try {
                try {
                    ClusData1 = URLDecoder.decode(ClusData1, "UTF-8");
                    JSONObject json = (JSONObject) JSONObject.parse(ClusData1);
                    ClueManagVo ClusData = null;
                    ClusData = json.toJavaObject(ClueManagVo.class);
                    resultVo = insertReceiveClueManag(ClusData, ClusData1, request);
                } catch (Exception e) {
                    resultVo.setStatus(ResultMsg.Result_Failed_Convert_Exception.getValue());
                    resultVo.setMsg(ResultMsg.Result_Failed_Convert_Exception.getMsg());
                    return resultVo;
                }


            } catch (Exception e) {
                resultVo.setStatus(ResultMsg.Result_Failed_Convert_Exception.getValue());
                resultVo.setMsg(ResultMsg.Result_Failed_Convert_Exception.getMsg());
            }
        } else {
            resultVo.setStatus(ResultMsg.Result_Parameter_IsNull.getValue());
            resultVo.setMsg(ResultMsg.Result_Parameter_IsNull.getMsg());
        }
        return resultVo;
    }

    /***
     * 拆分 insertReceiveClueManag
     * @param ClusData 接收字符串转实体
     * @param ClusData1 字符串
     * @param request
     * @return
     */
    public ResultVo<Long> insertReceiveClueManag(ClueManagVo ClusData, String ClusData1, HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        try {
            if (ClusData != null) {
                if (!"".equals(ClusData.getSign().trim()) && ClusData.getSign() != null) {
                    //根据渠道编码唯一标识取得渠道对象
                    UserChannel userChannel = getUserChannelByGUID(ClusData.getSign());
                    if (userChannel != null) {
                        // if (userChannel.getDisable() == 0) { //渠道是自动分发的
                        int intB = 1;
                        int intS = 1;
                        int intF = 1;
                        int intClueProper = 0;
                        String strClueProper = "";
                        String strClueCode = "";
                        String strPhone = "";
                        String userCode = "";
                        String numSection = "";//号码段
                        int intFlag = 0;
                        //resultVo= saveClueMangeDistable(userChannel,ClusData1,request,ClusData);
                        ResultListVo<ChannelPlatformRela> resultListVo1 = userChannelService.getChannelPlatFormRela(userChannel.getId());
                        if (resultListVo1 != null && resultListVo1.getList().size() > 0) {
                            intFlag = 2;
                        } else {
                            intFlag = 1;
                        }
                        ClueManag clueManag = new ClueManag();
                        clueManag.setChannelid(userChannel.getId());
                        //String ClueCode = userChannel.getChannelcode();
                        ReceiveLog receiveLog = new ReceiveLog();
                        receiveLog.setChannelcode(userChannel.getChannelcode());
                        receiveLog.setChannelname(userChannel.getChannelname());
                        receiveLog.setCreatetime(new Date());
                        receiveLog.setPushcontent(ClusData1);
                        try {
                            StringBuffer s = request.getRequestURL();
                            String PushUrl = s.toString();
                            receiveLog.setPushurl(PushUrl);
                        } catch (Exception e) {
                            String PushUrl = "";
                            receiveLog.setPushurl(PushUrl);
                        }
                        if (!"".equals(ClusData.getMileage()) && ClusData.getMileage() != null) {
                            BigDecimal bigMiileage = new BigDecimal(ClusData.getMileage().toString());
                            bigMiileage.setScale(2, BigDecimal.ROUND_HALF_UP);
                            clueManag.setMiileage(bigMiileage);
                        } else {
                            resultVo.setStatus(ResultMsg.Result_Km_NotNull.getValue());
                            resultVo.setMsg(ResultMsg.Result_Km_NotNull.getMsg());
                            return resultVo;
                        }
                        if (!"".equals(ClusData.getStyleName().trim()) && ClusData.getStyleName().trim() != null) {
                            clueManag.setStylename(ClusData.getStyleName().trim());
                        } else {
                            resultVo.setStatus(ResultMsg.Result_Vehicle_Type_NotNull.getValue());
                            resultVo.setMsg(ResultMsg.Result_Vehicle_Type_NotNull.getMsg());
                            return resultVo;
                        }
                        if (!"".equals(ClusData.getCityName().trim()) && ClusData.getCityName().trim() != null) {
                            clueManag.setCityname(ClusData.getCityName().trim());
                        } else {
                            resultVo.setStatus(ResultMsg.Result_CityName_NotNull.getValue());
                            resultVo.setMsg(ResultMsg.Result_CityName_NotNull.getMsg());
                            return resultVo;
                        }
                        if (!"".equals(ClusData.getRegDate().trim()) && ClusData.getRegDate().trim() != null) {
                            clueManag.setRegdate(getRegDate(ClusData.getRegDate().trim()));
                        } else {
                            resultVo.setStatus(ResultMsg.Result_Vehicle_Register_Date_NotNull.getValue());
                            resultVo.setMsg(ResultMsg.Result_Vehicle_Register_Date_NotNull.getMsg());
                            return resultVo;
                        }
                        if (!"".equals(ClusData.getClueType().trim()) && ClusData.getClueType().trim() != null) {
                            if (ClusData.getClueType().trim().equals(PublicConst.CLUEPROPER_BUY_STR)) {//买车
                                intClueProper = PublicConst.CLUEPROPER_BUY;
                                strClueCode = clueManagService.getClueCode(userChannel.getId(), PublicConst.CLUEPROPER_BUY).getData();
                                clueManag.setCluecode(GetChannlCode(strClueCode, ClusData.getClueType().trim(), intB, userChannel.getChannelcode()));
                                intB++;
                            } else if (ClusData.getClueType().trim().equals(PublicConst.CLUEPROPER_SELL_STR)) {//买车
                                intClueProper = PublicConst.CLUEPROPER_SELL;
                                strClueCode = clueManagService.getClueCode(userChannel.getId(), PublicConst.CLUEPROPER_SELL).getData();
                                clueManag.setCluecode(GetChannlCode(strClueCode, ClusData.getClueType().trim(), intS, userChannel.getChannelcode()));
                                intS++;
                            } else if (ClusData.getClueType().trim().equals(PublicConst.CLUEPROPER_FINANCE_STR)) {//金融
                                intClueProper = PublicConst.CLUEPROPER_FINANCE;
                                strClueCode = clueManagService.getClueCode(userChannel.getId(), PublicConst.CLUEPROPER_FINANCE).getData();
                                clueManag.setCluecode(GetChannlCode(strClueCode, ClusData.getClueType().trim(), intF, userChannel.getChannelcode()));
                                intF++;
                            }
                        } else {
                            resultVo.setStatus(ResultMsg.Result_Clue_Type_NotNull.getValue());
                            resultVo.setMsg(ResultMsg.Result_Clue_Type_NotNull.getMsg());
                            return resultVo;
                        }
                        if (!"".equals(ClusData.getContactsName().trim()) && ClusData.getContactsName().trim() != null) {
                            clueManag.setName(ClusData.getContactsName());
                        } else {
                            resultVo.setStatus(ResultMsg.Result_Contact_Person_NotNull.getValue());
                            resultVo.setMsg(ResultMsg.Result_Contact_Person_NotNull.getMsg());
                            return resultVo;
                        }
                        strPhone = ClusData.getContactsPhone().trim();
                        if (!"".equals(strPhone) && strPhone != null) {
                            if ((IsPhone(strPhone) && strPhone.length() == 11 && strPhone.startsWith("1"))) {
                                clueManag.setPhone(ClusData.getContactsPhone().trim());
                            } else {
                                resultVo.setStatus(119);
                                resultVo.setMsg("手机号码格式不正确");
                                return resultVo;
                            }
                        } else {
                            resultVo.setStatus(ResultMsg.Result_Contact_Phone_NotNull.getValue());
                            resultVo.setMsg(ResultMsg.Result_Contact_Phone_NotNull.getMsg());
                            return resultVo;
                        }
                        if (!"".equals(ClusData.getUserCode()) && ClusData.getUserCode() != null) {
                            userCode = ClusData.getUserCode();
                        }
                        clueManag.setClueproper(intClueProper);
                        clueManag.setUsercode(userCode);
                        clueManag.setStoragemode(PublicConst.STORAGEMODE_Interface);
                        clueManag.setPretreatment(PublicConst.PRETREATMENT);
                        clueManag.setStoragetime(new Date());
                        clueManag.setPushplatform("");
                        clueManag.setRemark("");
                        clueManag.setDisstatus(0);
                        if (userChannel.getDisable() == 0) {
                            clueManag.setIsautodis(0);
                        } else {
                            clueManag.setIsautodis(1);
                        }

                        if (clueManagService.existsClueManag(clueManag).getData() <= 0) {
                            int id = clueManagService.insertClueManagObject(clueManag);
                            if (id > 0) {
                                if (userChannel.getDisable() == 0) {
                                    try {
                                        //同渠道前五位号码段一分钟内入库超过8次不推送
                                        numSection = strPhone.substring(0, 5);
                                        int storageCount = 0;//入库次数
                                        int rubbishNumberCount = 0;//是否存在垃圾号码段
                                        ResultVo<String> resultVo1 = clueManagService.getClueStoragePhoneCountByChannelId(userChannel.getId(), numSection);
                                        if (resultVo1 != null && resultVo1.getData() != null) {
                                            storageCount = Integer.parseInt(resultVo1.getData());
                                        }
                                        ResultVo<Integer> resultVo2 = clueRubbishNumberService.getRubbishNumberByChannelid(userChannel.getId(), numSection);
                                        if (resultVo2 != null && resultVo2.getData() != null) {
                                            rubbishNumberCount = resultVo2.getData();
                                        }
                                        if (storageCount <= Integer.parseInt(rubbish_number) && rubbishNumberCount <= 0) {
                                            cluePushMqBiz.PushClusToPlatform(id, intFlag, userChannel.getId(), resultListVo1.getList());
                                        } else {
                                            //如果超过8次，但是没做记录，则保存垃圾号码段信息
                                            if (rubbishNumberCount <= 0) {
                                                saveRubbishNumber(userChannel.getId(), userChannel.getChannelname(), numSection);
                                            }
                                        }
                                        receiveLog.setStatus(1);
                                        receiveLogService.insertReceiveLog(receiveLog);
                                        resultVo.setData("线索Id:" + id);
                                        resultVo.setStatus(ResultMsg.Result_Push_Success.getValue());
                                        resultVo.setMsg(ResultMsg.Result_Push_Success.getMsg());
                                    } catch (Exception e) {
                                        receiveLog.setStatus(0);
                                        receiveLogService.insertReceiveLog(receiveLog);
                                        resultVo.setStatus(ResultMsg.Result_Push_Failed.getValue());
                                        resultVo.setMsg(ResultMsg.Result_Push_Failed.getMsg());
                                    }
                                } else {
                                    resultVo.setData("线索Id:" + id);
                                    resultVo.setStatus(ResultMsg.Result_Push_Success.getValue());
                                    resultVo.setMsg("线索接收成功！");
                                }

                            } else {
                                resultVo.setData("线索Id:" + id);
                                resultVo.setStatus(ResultMsg.Result_Push_Failed.getValue());
                                resultVo.setMsg("线索接收失败！");
                            }
                        } else {
                            resultVo.setStatus(ResultMsg.Result_Push_Repeat.getValue());
                            resultVo.setMsg(ResultMsg.Result_Push_Repeat.getMsg());
                        }
                                /*} else {
                                    //非自动分发的渠道
                                    resultVo = saveClueMangeDistable(userChannel, ClusData);
                                }*/
                    } else {
                        resultVo.setStatus(ResultMsg.Result_Channel_AuthCode_Error.getValue());
                        resultVo.setMsg(ResultMsg.Result_Channel_AuthCode_Error.getMsg());
                    }
                } else {
                    resultVo.setStatus(ResultMsg.Result_Channel_AuthCode_NotNull.getValue());
                    resultVo.setMsg(ResultMsg.Result_Channel_AuthCode_NotNull.getMsg());
                }
            } else {
                resultVo.setStatus(ResultMsg.Result_Failed_Check_Parameter.getValue());
                resultVo.setMsg(ResultMsg.Result_Failed_Check_Parameter.getMsg());
            }
        } catch (Exception e) {
            resultVo.setStatus(ResultMsg.Result_Failed_Check_Parameter.getValue());
            resultVo.setMsg(ResultMsg.Result_Failed_Check_Parameter.getMsg());
        }
        return resultVo;
    }

    /**
     * 保存垃圾号码段和记录发邮件短信
     *
     * @param channelid   渠道id
     * @param channelName 渠道名称
     * @param numSection  号码段
     */
    private void saveRubbishNumber(int channelid, String channelName, String numSection) {
        try {
            ClueRubbishNumber clueRubbishModel = new ClueRubbishNumber();
            clueRubbishModel.setChannelid(channelid);
            clueRubbishModel.setNumbersection(numSection);
            clueRubbishModel.setAddtime(new Date());
            int intCount = clueRubbishNumberService.saveClueRubbishNumberInfo(clueRubbishModel).getData();
            if (intCount > 0) {
                ClueSmsEmail clueSmsEmail = new ClueSmsEmail();
                clueSmsEmail.setIssend(0);
                clueSmsEmail.setMonitorid(channelid);
                clueSmsEmail.setMonitortype(PublicConst.PLATFORM_TYPE);
                clueSmsEmail.setAddtime(new Date());
                clueSmsEmail.setUpdatetime(new Date());
                String platform_Content = String.format(monitor_content, sdf.format(new Date()),
                        channelName, numSection, rubbish_number);
                clueSmsEmail.setSendcontent(platform_Content);
                //保存模板信息
                clueSmsEmailService.insertClueSmsEmail(clueSmsEmail);
            }
        } catch (Exception ex) {
            LOGGER.error("[clue-api][ReciveClueManagBiz][saveRubbishNumber] :保存垃圾号码段异常channelName：channelName={},numSection={}", channelName, numSection);
        }
    }

    /**
     * 不能包含文字和字母
     */
    public static boolean IsPhone(String input) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(input).matches();
    }

    /**
     * 根据线索id返回上架成交状态
     *
     * @param clueid
     * @param channelid
     * @return
     */
    public ResultVo getClueManagById(int clueid, String channelid) {
        ResultVo resultVo = new ResultVo();
        //过滤不让获取状态的渠道
        if (!StringUtils.isEmpty(channelid) && getStatusNoAuthchannelIdList.indexOf("," + channelid) < 0) {
            resultVo = getClueManagById(clueid);
        } else {
            resultVo = null;
        }
        return resultVo;
    }

    /**
     * 获取二手车之家线索
     *
     * @return
     */
    public ResultVo carEstimateQueryData(HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        String url = "";
        int intCount = 0;
        try {
            CarEstimateResVo carEstimateResVo;
            String newKey = Md5Encrypt.toHexString(Md5Encrypt.encrypt(CarEstimate_key, "UTF-8"));
            url = CarEstimate_queryURL + "?key=" + newKey;
            LOGGER.info("[clue-api][ReciveClueManagBiz][carEstimateQueryData] :获取二手车之家线索url={}", url);
            String res = HttpUtils.doGet(url);
            if (StringUtils.isNotEmpty(res)) {
                carEstimateResVo = JSON.toJavaObject(JSON.parseObject(res), CarEstimateResVo.class);
                LOGGER.info("[clue-api][ReciveClueManagBiz][carEstimateQueryData] :获取二手车之家线索 获取条数={}", carEstimateResVo.getResult().size());
                if (carEstimateResVo.getReturncode() == 0 && carEstimateResVo.getResult().size() > 0) {
                    //循环插入数据库并放入队列
                    for (CarEstimateResVo.ResultEntity resultEntity : carEstimateResVo.getResult()) {
                        //判断城市是否符合规则
                        Boolean IsCityName = CarEstimate_city.indexOf(resultEntity.getCityName()) >= 0;
                        if (IsCityName) {
                            insertReceiveClueManag(getClueManagVo(resultEntity), JSON.toJSONString(resultEntity), request);
                            intCount++;
                        }
                    }
                    //回调回传状态接口，回写以获取状态
                    updateState(carEstimateResVo.getResult());
                } else {
                    LOGGER.info("[clue-api][ReciveClueManagBiz][carEstimateQueryData] :获取二手车之家线索信息  res={}", JSON.toJSONString(res));
                }
            }
            else {
                LOGGER.info("[clue-api][ReciveClueManagBiz][carEstimateQueryData] :没有要获取二手车之家线索数据  res={}", JSON.toJSONString(res));
            }
        } catch (Exception ex) {
            LOGGER.error("[clue-api][ReciveClueManagBiz][carEstimateQueryData] :获取二手车之家线索接口 。" + "传入参数：queryData={},异常信息=", url, ex);
        }
        resultVo.setMsg("获取线索成功");
        resultVo.setStatus(ResultMsg.Result_Success.getValue());
        resultVo.setData(intCount);
        return resultVo;
    }

    /**
     * 获取线索入库成功后，回传以获取状态
     *
     * @param resultEntityList
     */
    private void updateState(List<CarEstimateResVo.ResultEntity> resultEntityList) {
        for (CarEstimateResVo.ResultEntity resultEntity : resultEntityList) {
            //判断城市是否符合规则
            Boolean IsCityName = CarEstimate_city.indexOf(resultEntity.getCityName()) >= 0;
            if (IsCityName) {
                doGetUpdateState(resultEntity.getCclid(), 5);
            }
        }
    }

    /**
     * 给二手之家线索回传状态
     *
     * @return
     */
    public ResultVo carEstimateUpdateState() {
        ResultVo resultVo = new ResultVo();
        int intCout = 0;
        try {
            //获取回传状态数据源
            ResultListVo<Map<String, Object>> list = clueManagService.getErShouCheZhiJiaClueState(CarEstimate_sign);
            if (list != null && list.getList().size() > 0) {
                int zhiJiaState = 0;
                int state = 0;
                int resultCount = 0;
                int cclid = 0;
                for (Map<String, Object> map : list.getList()) {
                    cclid = Integer.parseInt(map.get("UserCode").toString());
                    state = Integer.parseInt(map.get("ClueState").toString());
                    System.out.println(map.get("UserCode") + "====" + map.get("ClueState"));
                    if (!"".equals(map.get("UserCode")))//usercode此处存储的是cclid
                    {
                        if (state == 2) {
                            zhiJiaState = 20;//上架成功
                        } else {
                            zhiJiaState = 35;//交易成功
                        }
                        //判断是否已经回传过状态,如果没有推送过则推送
                        if (!clueManagService.existsErShouCheZhiJiaCclid(cclid, state).getData()) {
                            resultCount = doGetUpdateState(cclid, zhiJiaState);
                            //回传状态成功，记录线索id和回传状态
                            if (resultCount > 0) {
                                insertClueErShouCheZhiJiaRecord(cclid, state);
                                intCout = intCout + resultCount;
                            }
                        }
                    }
                }
            } else {
                resultVo.setMsg("暂无回传状态数据");
                resultVo.setStatus(ResultMsg.Result_Success.getValue());
                return resultVo;
            }
        } catch (Exception ex) {
            LOGGER.error("[clue-api][ReciveClueManagBiz][carEstimateUpdateState] :回传二手车之家线索状态异常信息=", ex);
            resultVo.setMsg("回传状态失败");
            resultVo.setStatus(500);
            return resultVo;
        }
        resultVo.setMsg("回传状态成功");
        resultVo.setData(intCout);
        resultVo.setStatus(ResultMsg.Result_Success.getValue());
        return resultVo;
    }

    /**
     * 保存回传二手车之家上架、成交记录
     *
     * @param userCode 之家线索id
     * @param status   回传状态
     */
    private void insertClueErShouCheZhiJiaRecord(int userCode, int status) {
        ClueErShouCheZhiJiaRecord clueZhiJiaRecord = new ClueErShouCheZhiJiaRecord();
        clueZhiJiaRecord.setCclid(userCode);
        clueZhiJiaRecord.setCluestate(status);
        clueZhiJiaRecord.setAddtime(new Date());
        clueManagService.insertClueErShouCheZhiJiaRecord(clueZhiJiaRecord);
    }

    /**
     * 公用方法，回传线索状态
     *
     * @param cclid
     * @param statue
     */
    private int doGetUpdateState(int cclid, int statue) {
        CarEstimateResVo carEstimateResVo;
        int intCout = 0;
        String newKey = Md5Encrypt.toHexString(Md5Encrypt.encrypt(CarEstimate_key, "UTF-8"));
        String url = CarEstimate_updateStateURL + "?key=" + newKey;
        try {
            url = url + "&cclid=" + cclid + "&statue=5";
            String res = HttpUtils.doGet(url);
            if (StringUtils.isNotEmpty(res)) {
                carEstimateResVo = JSON.toJavaObject(JSON.parseObject(res), CarEstimateResVo.class);
                if (carEstimateResVo.getReturncode() == 0) {
                    intCout++;
                } else {
                    LOGGER.info("[clue-api][ReciveClueManagBiz][doGetUpdateState] :回传二手车之家线索状态失败  res={}", JSON.toJSONString(res));
                }
            } else {
                LOGGER.info("[clue-api][ReciveClueManagBiz][doGetUpdateState] :回传二手车之家线索状态返回空  url={}，res={}", url, res);
            }
        } catch (Exception ex) {
            LOGGER.error("[clue-api][ReciveClueManagBiz][doGetUpdateState] :回传二手车之家线索状态异常 。" + "传入参数：queryData={},异常信息=", url, ex);
        }
        return intCout;
    }

    private ClueManagVo getClueManagVo(CarEstimateResVo.ResultEntity resultEntity) {
        ClueManagVo clueManagVo = new ClueManagVo();
        clueManagVo.setSign(CarEstimate_sign);
        clueManagVo.setUserCode(String.valueOf(resultEntity.getCclid())); //存储对方id
        clueManagVo.setCityName(resultEntity.getCityName());
        clueManagVo.setClueType(PublicConst.CLUEPROPER_SELL_STR);
        if ("".equals(resultEntity.getSpecName())) {
            clueManagVo.setStyleName("未知");
        } else {
            clueManagVo.setStyleName(resultEntity.getSpecName());
        }
        if ("".equals(resultEntity.getName())) {
            clueManagVo.setContactsName("车主");
        } else {
            clueManagVo.setContactsName(resultEntity.getName());
        }
        clueManagVo.setContactsPhone(resultEntity.getMobile());
        BigDecimal bigMiileage = new BigDecimal(resultEntity.getMileage());
        bigMiileage.setScale(2, BigDecimal.ROUND_HALF_UP);
        clueManagVo.setMileage(bigMiileage);// 公里数(万)
        if ("".equals(resultEntity.getFirstRegistrationTimeStr())) {
            clueManagVo.setRegDate("未知");
        } else {
            clueManagVo.setRegDate(resultEntity.getFirstRegistrationTimeStr());
        }
        return clueManagVo;
    }

    /**
     * 根据线索id查询爱卡数据是否重复
     *
     * @param clueid
     * @param channelid
     * @return
     */
    public ResultVo getClueManagAKById(int clueid, String channelid) {
        ResultVo resultVo = new ResultVo();
        //过滤不让获取状态的渠道
        if (!StringUtils.isEmpty(channelid) && getStatusNoAuthchannelIdList.indexOf("," + channelid) < 0) {
            resultVo = getClueStatusAKById(clueid, Integer.parseInt(channelid));
        } else {
            resultVo = null;
        }
        return resultVo;
    }

    /**
     * 根据线索id返回上架成交状态
     *
     * @param clueid
     * @return
     */
    public ResultVo getClueManagById(int clueid) {
        LOGGER.debug("[clue-api][ReciveClueManagBiz][getClueManagById] :根据线索id返回上架成交状态开始。" + "传入参数：clueid={}", clueid);

        ResultVo resultVo = new ResultVo();
        try {
            ResultVo<ClueManag> resultVo1 = clueManagService.getClueManagById(clueid);
            if (resultVo1.getData() != null) {
                ClueManag clueManag = resultVo1.getData();
                if (clueManag.getIsautodis() == null || clueManag.getIsautodis() == 0) {
                    ResultListVo<ClueStatus> resultListVo = clueStatusService.getClueStatusList(clueid);
                    List<ClueStatus> clueStatusList = resultListVo.getList();
                    int yishangjia = 0;
                    int yichengjiao = 0;
                    for (ClueStatus clueStatus : clueStatusList) {
                        if (clueStatus.getCluestate() == 2) {
                            yishangjia++;
                        } else if (clueStatus.getCluestate() == 3) {
                            yichengjiao++;
                        }
                    }
                    if (yichengjiao > 0) {
                        ClueStatusDao clueStatusDao = new ClueStatusDao();
                        clueStatusDao.setGrounding("上架");
                        clueStatusDao.setGroundingnum(yishangjia);
                        clueStatusDao.setDeal("成交");
                        resultVo.setData(clueStatusDao);
                        resultVo.setStatus(100);
                    } else {
                        if (yishangjia > 0) {
                            ClueStatusDao clueStatusDao = new ClueStatusDao();
                            clueStatusDao.setGrounding("上架");
                            clueStatusDao.setGroundingnum(yishangjia);
                            clueStatusDao.setDeal("");
                            resultVo.setData(clueStatusDao);
                            resultVo.setStatus(100);
                        } else {
                            resultVo.setMsg("待处理");
                            resultVo.setStatus(100);
                        }
                    }
                } else {
                    resultVo.setMsg("待处理");
                    resultVo.setStatus(100);
                }
            } else {
                resultVo.setMsg("检测您的线索id是否正确");
                resultVo.setStatus(101);
            }
        } catch (Exception ex) {
            resultVo.setMsg("获取线索数据异常" + ex.getMessage());
            resultVo.setStatus(102);
            LOGGER.error("[clue-api][ReciveClueManagBiz][getClueManagById] :根据线索id返回上架成交状态异常。" + "传入参数：clueid={}，异常信息=", clueid, ex);

        }
        LOGGER.debug("[clue-api][ReciveClueManagBiz][getClueManagById] :根据线索id返回上架成交状态结束。" + "传入参数：clueid={}", clueid);

        return resultVo;
    }

    /**
     * 根据线索id查询爱卡数据是否重复
     *
     * @param clueid
     * @return
     */
    public ResultVo getClueStatusAKById(int clueid, int channelId) {
        LOGGER.info("[clue-api][ReciveClueManagBiz][getClueManagAKById] :根据线索id查询爱卡数据是否重复开始。" + "传入参数：clueid={}", clueid);

        ResultVo resultVo = new ResultVo();
        try {
            //检查是否有此线索
            ResultVo<ClueManag> resultVo1 = clueManagService.getClueManagById(clueid);
            if (resultVo1.getData() != null) {
                ClueManag clueManag = resultVo1.getData();
                //线索是否自动分发
                if (clueManag.getIsautodis() == null || clueManag.getIsautodis() == 0) {
                    Boolean IsHasCityName = aikaCovingCityList.indexOf(clueManag.getCityname()) >= 0;
                    if (IsHasCityName) {
                        //查询线索状态未在规定时间内，请稍后查询。
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                        long min = getDatePoor(df.parse(df.format(new Date())), df.parse(df.format(clueManag.getStoragetime())));
                        if (min > PublicConst.GUAZI_MIN) {
                            //获取推送平台，天天拍、瓜子
                            ResultListVo<SalesCluePushMQModel> list = clueManagService.getCluePushListMqByManagId(clueid, 4);
                            ResultVo<ReturnInfoDto> resultVo2 = new ResultVo<ReturnInfoDto>();
                            ResultVo<ClueStatus> resultVo3 = new ResultVo<ClueStatus>();
                            int flag = 0;
                            if (list != null && list.getList().size() > 0) {
                                //如果只推送天天拍，重复则直接返回
                                if (list.getList().size() == 1 && list.getList().get(0).getPlatformValue() == PublicConst.PLATFORM_TTPAI) {
                                    return getTTPCStatus(clueid, channelId);
                                }
                                //只推给瓜子平台
                                if (list.getList().size() == 1 && list.getList().get(0).getPlatformValue() == PublicConst.PLATFORM_GUAZI) {
                                    return getGuaZiStatus(clueid, channelId);
                                }
                                for (SalesCluePushMQModel salesCluePushMQModel : list.getList()) {
                                    if (salesCluePushMQModel.getPlatformValue() == PublicConst.PLATFORM_TTPAI) {
                                        //获取推送天天拍状态
                                        resultVo2 = pushLogService.getPushLogReturnInfo(clueid, channelId);
                                        if (resultVo2.getData() != null) {
                                            //如果天天拍推送失败
                                            if (resultVo2.getData().getError()) {
                                                String message = resultVo2.getData().getMessage();
                                                if (message.indexOf(PublicConst.RESULT_MESSAGE) != -1 || message.indexOf(PublicConst.RESULT_MESSAGE1) != -1) {
                                                    flag = 2;
                                                } else {
                                                    flag = 3;
                                                }
                                            } else {
                                                //推送成功
                                                flag = 1;
                                            }
                                        }
                                    }
                                    //推送给瓜子
                                    if (salesCluePushMQModel.getPlatformValue() == PublicConst.PLATFORM_GUAZI) {
                                        resultVo3 = clueStatusService.queryClueStatusByClueIdAndChannelId(clueid, channelId);
                                        if (resultVo3.getData() != null) {
                                            //如果天天拍推送重复，过滤失败或重复，则数据重复(两个平台都重复为重复)
                                            if (flag == 2 && (resultVo3.getData().getPlatformstatusid() == PublicConst.PLATFORM_GUAZI_77
                                                    || resultVo3.getData().getPlatformstatusid() == PublicConst.PLATFORM_GUAZI_78)) {
                                                resultVo.setMsg(ResultMsg.Result_AK_Repeat.getMsg());
                                                resultVo.setStatus(ResultMsg.Result_AK_Repeat.getValue());
                                            }
                                            //推送了天天拍成功,推送瓜子反馈过滤失败和重复，则数据重复（两个平台任意不重复为成功）
                                            if (flag == 1 && (resultVo3.getData().getPlatformstatusid() == PublicConst.PLATFORM_GUAZI_77 ||
                                                    resultVo3.getData().getPlatformstatusid() == PublicConst.PLATFORM_GUAZI_78)) {
                                                resultVo.setMsg(ResultMsg.Result_AK_Success.getMsg());
                                                resultVo.setStatus(ResultMsg.Result_AK_Success.getValue());
                                            }
                                            //如果天天拍推送重复，瓜子成功不重复，则数据有效（两个平台任意不重复为成功）
                                            if ((flag == 1 || flag == 2) && (resultVo3.getData().getPlatformstatusid() != PublicConst.PLATFORM_GUAZI_77
                                                    && resultVo3.getData().getPlatformstatusid() != PublicConst.PLATFORM_GUAZI_78
                                                    && resultVo3.getData().getPlatformstatusid() != PublicConst.PLATFORM_GUAZI_74
                                                    && resultVo3.getData().getPlatformstatusid() != PublicConst.PLATFORM_GUAZI_76)) {
                                                resultVo.setMsg(ResultMsg.Result_AK_Success.getMsg());
                                                resultVo.setStatus(ResultMsg.Result_AK_Success.getValue());
                                            }
                                            if (flag == 3) {
                                                resultVo.setMsg(ResultMsg.Result_AK_Malformed_Phone.getMsg());
                                                resultVo.setStatus(ResultMsg.Result_AK_Malformed_Phone.getValue());
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            resultVo.setMsg(ResultMsg.Result_AK_WaitingStorage.getMsg());
                            resultVo.setStatus(ResultMsg.Result_AK_WaitingStorage.getValue());
                        }
                    } else {
                        resultVo.setMsg(ResultMsg.Result_AK_NotCity.getMsg());
                        resultVo.setStatus(ResultMsg.Result_AK_NotCity.getValue());
                    }
                } else {
                    resultVo.setMsg(ResultMsg.Result_AK_Pending.getMsg());
                    resultVo.setStatus(ResultMsg.Result_AK_Pending.getValue());
                }
            } else {
                resultVo.setMsg(ResultMsg.Result_AK_NotExist.getMsg());
                resultVo.setStatus(ResultMsg.Result_AK_NotExist.getValue());
            }
        } catch (Exception ex) {
            resultVo.setMsg("获取线索数据异常" + ex.getMessage());
            resultVo.setStatus(102);
            LOGGER.error("[clue-api][ReciveClueManagBiz][getClueManagAKById] :根据线索id查询爱卡数据是否重复状态异常。" + "传入参数：clueid={}，异常信息=", clueid, ex);
        }
        LOGGER.info("[clue-api][ReciveClueManagBiz][getClueManagAKById] :根据线索id查询爱卡数据是否重复状态结束。" + "传入参数：clueid={}", clueid);

        return resultVo;
    }

    /***
     * 爱卡只推送天天拍平台
     * @param clueid
     * @param channelId
     * @return
     */
    private ResultVo getTTPCStatus(int clueid, int channelId) {
        ResultVo resultVo = new ResultVo();
        ResultVo<ReturnInfoDto> resultVo2 = pushLogService.getPushLogReturnInfo(clueid, channelId);
        if (resultVo2.getData() != null) {
            //如果天天拍推送失败
            if (resultVo2.getData().getError()) {
                String message = resultVo2.getData().getMessage();
                if (message.indexOf(PublicConst.RESULT_MESSAGE) != -1 || message.indexOf(PublicConst.RESULT_MESSAGE1) != -1) {
                    resultVo.setMsg(ResultMsg.Result_AK_Repeat.getMsg());
                } else {
                    resultVo.setMsg(resultVo2.getData().getMessage());
                }
                resultVo.setStatus(200);
            } else {
                //入库成功，不重复
                resultVo.setMsg(ResultMsg.Result_AK_Success.getMsg());
                resultVo.setStatus(ResultMsg.Result_AK_Success.getValue());
            }
        }
        return resultVo;
    }

    /***
     * 爱卡只推送天瓜子平台
     * @param clueid
     * @param channelId
     * @return
     */
    private ResultVo getGuaZiStatus(int clueid, int channelId) {
        ResultVo resultVo = new ResultVo();
        ResultVo<ClueStatus> resultVo3 = clueStatusService.queryClueStatusByClueIdAndChannelId(clueid, channelId);
        if (resultVo3.getData() != null) {
            //如果反馈过滤失败或者重复，则都算数据重复。
            if (resultVo3.getData().getPlatformstatusid() == PublicConst.PLATFORM_GUAZI_77 || resultVo3.getData().getPlatformstatusid() == PublicConst.PLATFORM_GUAZI_78) {
                resultVo.setMsg(ResultMsg.Result_AK_Repeat.getMsg());
            }
            if (resultVo3.getData().getPlatformstatusid() != PublicConst.PLATFORM_GUAZI_77
                    && resultVo3.getData().getPlatformstatusid() != PublicConst.PLATFORM_GUAZI_78
                    && resultVo3.getData().getPlatformstatusid() != PublicConst.PLATFORM_GUAZI_74
                    && resultVo3.getData().getPlatformstatusid() != PublicConst.PLATFORM_GUAZI_76) {
                resultVo.setMsg(ResultMsg.Result_AK_Success.getMsg());
            }
            resultVo.setStatus(200);
        }
        return resultVo;
    }


    public static long getDatePoor(Date endDate, Date nowDate) {
        // 计算差多少分钟
        long min = Math.abs(endDate.getTime() - nowDate.getTime()) / (1000 * 60);
        return min;
    }

    /***
     * 处理线索编号
     * @param strClueCode
     * @param strClueProper
     * @param intCount
     * @param strChannelCode
     * @return
     */
    public static String GetChannlCode(String strClueCode, String strClueProper, int intCount, String strChannelCode) {
        String strChannlCode = "";
        String strResultCode = "";
        if (strClueCode != null && !"".equals(strClueCode)) {
            //如果是买家
            if (strClueProper.equals("买车")) {
                //数据库是否有买车数据
                strChannlCode = strClueCode;
                //GetListCode(resultListVo, PublicConst.CLUEPROPER_BUY);
                if (!"".equals(strChannlCode)) {
                    strResultCode = GetCode(strChannlCode);
                } else {
                    strResultCode = GetNewCode(PublicConst.CLUECODE_BUY, strChannelCode);
                }
            } else if (strClueProper.equals("卖车"))//如果是卖家
            {
                //数据库是否有卖车数据
                strChannlCode = strClueCode;
                //GetListCode(resultListVo, PublicConst.CLUEPROPER_SELL);
                if (!"".equals(strChannlCode)) {
                    strResultCode = GetCode(strChannlCode);
                } else {
                    strResultCode = GetNewCode(PublicConst.CLUECODE_SELL, strChannelCode);
                }
            } else if (strClueProper.equals("金融"))//如果是金融
            {
                //数据库是否有金融数据
                strChannlCode = strClueCode;
                //GetListCode(resultListVo, PublicConst.CLUEPROPER_FINANCE);
                if (!"".equals(strChannlCode)) {
                    strResultCode = GetCode(strChannlCode);
                } else {
                    strResultCode = GetNewCode(PublicConst.CLUECODE_FINANCE, strChannelCode);
                }
            }
        } else {
            strResultCode = GetResultCode(strClueProper, strChannelCode);
        }
        return strResultCode;
    }


    /***
     * 如果数据库没有属性数据时
     * @param strClueProper
     * @param strChannelCode
     * @return
     */
    private static String GetResultCode(String strClueProper, String strChannelCode) {
        String strResultCode = "";
        if (strClueProper.equals("买车")) {
            strResultCode = GetNewCode(PublicConst.CLUECODE_BUY, strChannelCode);
        } else if (strClueProper.equals("卖车"))//如果是卖家
        {
            strResultCode = GetNewCode(PublicConst.CLUECODE_SELL, strChannelCode);
        } else if (strClueProper.equals("金融"))//如果是金融
        {
            strResultCode = GetNewCode(PublicConst.CLUECODE_FINANCE, strChannelCode);
        }
        return strResultCode;
    }

    /***
     * 如果数据库中存在编码
     * @param strChannlCode
     * @return
     */
    private static String GetCode(String strChannlCode) {
        String strResultCode = "";
        if (!"".equals(strChannlCode)) {
            strResultCode = strChannlCode.split("-")[2];
            int intA = Integer.parseInt(strResultCode) + 1;
            strResultCode = padRight(String.valueOf(intA), 11, '0');//不足补0
            strResultCode = strChannlCode.split("-")[0] + "-" + strChannlCode.split("-")[1] + "-" + strResultCode;
        }
        return strResultCode;
    }

    /***
     *  获取线索编号
     * @param strChannlCode
     * @param strChanCode
     * @return
     */
    private static String GetNewCode(String strChannlCode, String strChanCode) {
        String strResultCode = "";
        if (!"".equals(strChannlCode)) {
            strResultCode = strChannlCode.split("-")[1];
            int intA = Integer.parseInt(strResultCode) + 1;
            strResultCode = padRight(String.valueOf(intA), 11, '0');//不足补0
            strResultCode = strChanCode + "-" + strChannlCode.split("-")[0] + "-" + strResultCode;
        }
        return strResultCode;
    }

    /**
     * 时间处理
     */
    private String getRegDate(String regDate) {
        String strRegDate = "";
        Date date = new Date();
        int flag = 0;
        try {
            try {
                date = format.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format1.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format2.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format3.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format4.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format5.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            if (flag < 6) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                strRegDate = cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月";
            } else {
                strRegDate = regDate;
            }
        } catch (Exception e) {
            strRegDate = regDate;
        }
        return strRegDate;
    }

    /**
     * 右补位，左对齐
     *
     * @param oriStr 原字符串
     * @param len    目标字符串长度
     * @param alexin 补位字符
     * @return 目标字符串
     */
    public static String padRight(String oriStr, int len, char alexin) {
        String str = "";
        int strlen = oriStr.length();
        if (strlen < len) {
            for (int i = 0; i < len - strlen; i++) {
                str = str + alexin;
            }
        }
        str = str + oriStr;
        return str;
    }

    /**
     * 左补位，右对齐
     *
     * @param oriStr 原字符串
     * @param len    目标字符串长度
     * @param alexin 补位字符
     * @return 目标字符串
     */
    public static String padLeft(String oriStr, int len, char alexin) {
        String str = "";
        int strlen = oriStr.length();
        if (strlen < len) {
            for (int i = 0; i < len - strlen; i++) {
                str = str + alexin;
            }
        }
        str = oriStr + str;
        return str;
    }


}

package com.jzg.clue.api.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jzg.base.composite.service.api.CarStyleBaseService;
import com.jzg.clue.api.util.CharUtils;
import com.jzg.clue.api.util.DateUtils;
import com.jzg.clue.api.util.PublicConst;
import com.jzg.clue.api.util.ResultMsg;
import com.jzg.clue.api.vo.ClueIdResVo;
import com.jzg.clue.api.vo.ClueManagVo;
import com.jzg.clue.service.api.ClueManagService;
import com.jzg.clue.service.api.ReceiveLogService;
import com.jzg.clue.service.api.UserChannelService;
import com.jzg.clue.service.model.ChannelPlatformRela;
import com.jzg.clue.service.model.ClueManag;
import com.jzg.clue.service.model.ReceiveLog;
import com.jzg.clue.service.model.UserChannel;
import com.jzg.framework.core.vo.ResultListVo;
import com.jzg.framework.core.vo.ResultVo;
import com.jzg.framework.core.vo.RetStatus;
import com.jzg.thirdparty.vo.carstyle.CarMakeVo;
import com.jzg.thirdparty.vo.carstyle.RequestVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;

/**
 * Created by mush on 2017/06/02.
 */
@Component("reciveBxwClueManagBiz")
public class ReciveBxwClueManagBiz {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReciveBxwClueManagBiz.class);


    /***
     * userChannelService 声明
     */
    @Resource
    private UserChannelService userChannelService;

    /***
     * receiveLogService 声明
     */
    @Resource
    private ReceiveLogService receiveLogService;

    /***
     * cluePushMqBiz 声明
     */
    @Resource
    private CluePushMqBiz cluePushMqBiz;

    /***
     * clueManagService 引用声明
     */
    @Resource
    private ClueManagService clueManagService;

    /***
     * 车型引用
     */
    @Resource
    private CarStyleBaseService carStyleBaseService;

    /**
     * thirdparty 下requestVo
     */
    @Resource
    private  RequestVo requestVo;

    /**
     * * 根据用户渠道获取一小时之内的线索数量
     *
     * @param clusDataStr
     * @param request
     * @return ｛status:100, data:{}, msg:null｝
     */

    public ResultVo<Long> getReceiveClueCountByChannelCode(String clusDataStr, HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        if (clusDataStr != null) {
            try {
                clusDataStr = URLDecoder.decode(clusDataStr, PublicConst.BASE_ENCODING);
                JSONObject json = (JSONObject) JSONObject.parse(clusDataStr);
                ClueManagVo clueData = json.toJavaObject(ClueManagVo.class);
                try {
                    if (clueData != null) {
                        if (StringUtils.isNotBlank(clueData.getChannelCode())) {
                            Integer count = clueManagService.getReceiveClueCountByChannelCode(clueData.getChannelCode()).getData();
                            resultVo.setData(count);
                            if (count > 0) {
                                resultVo.setStatus(ResultMsg.Result_Success.getValue());
                                resultVo.setMsg(ResultMsg.Result_Success.getMsg());
                            } else {
                                resultVo.setStatus(ResultMsg.Result_Channel_Send_IsNull.getValue());
                                resultVo.setMsg(ResultMsg.Result_Channel_Send_IsNull.getMsg());
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
                    LOGGER.error("[clue-api][ReciveBxwClueManagBiz][getReceiveClueCountByChannelCode] :根据用户渠道获取一小时之内的线索数量." + "传入参数：clusDataStr={},异常信息=", clusDataStr, e);

                    resultVo.setStatus(ResultMsg.Result_Failed_Check_Parameter.getValue());
                    resultVo.setMsg(ResultMsg.Result_Failed_Check_Parameter.getMsg());
                }
            } catch (Exception e) {
                LOGGER.error("[clue-api][ReciveBxwClueManagBiz][getReceiveClueCountByChannelCode] :根据用户渠道获取一小时之内的线索数量." + "传入参数：clusDataStr={},异常信息=", clusDataStr, e);

                resultVo.setStatus(ResultMsg.Result_Failed_Convert_Exception.getValue());
                resultVo.setMsg(ResultMsg.Result_Failed_Convert_Exception.getMsg());
            }
        } else {
            //重复的定义
            resultVo.setStatus(ResultMsg.Result_Parameter_IsNull.getValue());
            resultVo.setMsg(ResultMsg.Result_Parameter_IsNull.getMsg());
        }
        if (resultVo.getData() == null) {
            resultVo.setData("");
        }
        return resultVo;
    }

    /**
     * 收集线索接
     *
     * @param clusDataStr
     * @param request
     * @return ｛status:100, data:{}, msg:null｝
     */
    public ResultVo<Long> insertReceiveBxwClueManag(String clusDataStr, HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        if (clusDataStr != null) {
            try {
                clusDataStr = URLDecoder.decode(clusDataStr, PublicConst.BASE_ENCODING);
                JSONObject json = (JSONObject) JSONObject.parse(clusDataStr);
                ClueManagVo clueData = json.toJavaObject(ClueManagVo.class);
                try {
                    if (clueData != null) {
                        if (StringUtils.isNotBlank(clueData.getSign())) {
                            //根据渠道编码唯一标识取得渠道对象
                            UserChannel userChannel = userChannelService.getUserChannelByGUID(clueData.getSign()).getData();
                            if (userChannel != null) {
                                //验证参数
                                ValidateParams validate = new ValidateParams(clusDataStr, request, resultVo, clueData, userChannel).validate();
                                if (validate.is()) {
                                    return resultVo;
                                }
                                int sendType = userChannel.getDisable();
                                resultVo = sendData(resultVo, clueData, userChannel, validate, sendType);
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
                    LOGGER.error("[clue-api][ReciveBxwClueManagBiz][insertReceiveBxwClueManag] :插入百姓网线索." + "传入参数：clusDataStr={},异常信息=", clusDataStr, e);

                    resultVo.setStatus(ResultMsg.Result_Failed_Check_Parameter.getValue());
                    resultVo.setMsg(ResultMsg.Result_Failed_Check_Parameter.getMsg());
                }
            } catch (Exception e) {
                LOGGER.error("[clue-api][ReciveBxwClueManagBiz][insertReceiveBxwClueManag] :插入百姓网线索." + "传入参数：clusDataStr={},异常信息=", clusDataStr, e);

                resultVo.setStatus(ResultMsg.Result_Failed_Convert_Exception.getValue());
                resultVo.setMsg(ResultMsg.Result_Failed_Convert_Exception.getMsg());
            }
        } else {
            resultVo.setStatus(ResultMsg.Result_Parameter_IsNull.getValue());//重复的定义
            resultVo.setMsg(ResultMsg.Result_Parameter_IsNull.getMsg());
        }
        if (resultVo.getData() == null) {
            resultVo.setData("");
        }
        return resultVo;
    }

    /**
     * 组装并推送数据
     *
     * @param resultVo
     * @param clusData
     * @param userChannel
     * @param validate
     * @param sendType    先审后发; 0:直接分发;
     */
    private ResultVo sendData(ResultVo resultVo, ClueManagVo clusData, UserChannel userChannel, ValidateParams validate, int sendType) {
        //组装线索参数
        SetClueParams setClueParams = new SetClueParams(clusData, validate, sendType).invoke();
        ClueManag clueManag = setClueParams.getClueManag();
        int intFlag = setClueParams.getIntFlag();
        ResultListVo<ChannelPlatformRela> resultListVo1 = setClueParams.getResultListVo1();
        ReceiveLog receiveLog = setClueParams.getReceiveLog();
        //操作线索逻辑
        if (clueManagService.existsBxwClueCityName(clueManag).getStatus() == RetStatus.Ok.getValue()) {
            //if(clueManagService.existsBxwClueStyleName(clueManag).getStatus() == RetStatus.Ok.getValue()){
            if (existsBxwClueMakeName(clueManag.getStylename()).getStatus() != RetStatus.Ok.getValue()) {
                if (clueManagService.existsBxwClueManag(clueManag).getStatus() != RetStatus.Ok.getValue()) {
                    int id = clueManagService.insertClueManagObject(clueManag);
                    if (id > 0) {
                        try {
                            if (sendType == PublicConst.CHANNEL_SEND_TYPE_DIRECT) {
                                //添加自动分发到队列
                                cluePushMqBiz.PushClusToPlatform(id, intFlag, userChannel.getId(), resultListVo1.getList());
                            }
                            receiveLog.setStatus(PublicConst.RECEIVELOG_SUCCESS);
                            ClueIdResVo clueIdResVo = new ClueIdResVo();
                            clueIdResVo.setClueId(id);
                            resultVo.setData(clueIdResVo);
                            resultVo.setStatus(ResultMsg.Result_Push_Success.getValue());
                            resultVo.setMsg(ResultMsg.Result_Push_Success.getMsg());
                        } catch (Exception e) {
                            receiveLog.setStatus(PublicConst.RECEIVELOG_fAILED);
                            resultVo.setStatus(ResultMsg.Result_Push_Failed.getValue());
                            resultVo.setMsg(ResultMsg.Result_Push_Failed.getMsg());
                            LOGGER.error("[clue-api][ReciveBxwClueManagBiz][sendData] :组装并推送数据." + "传入参数：resultVo={},ClueManagVo={},userChannel={},ValidateParams={},sendType={},异常信息=", JSON.toJSONString(resultVo), JSON.toJSONString(clusData), JSON.toJSONString(userChannel), JSON.toJSONString(validate), sendType, e);

                        } finally {
                            receiveLogService.insertReceiveLog(receiveLog);
                        }
                    }
                } else {
                    resultVo.setStatus(ResultMsg.Result_Push_Repeat.getValue());
                    resultVo.setMsg(ResultMsg.Result_Push_Repeat.getMsg());
                }
            } else {
                resultVo.setStatus(ResultMsg.Result_Push_Brand_Invalid.getValue());
                resultVo.setMsg(ResultMsg.Result_Push_Brand_Invalid.getMsg());
            }
        } else {
            resultVo.setStatus(ResultMsg.Result_Push_CityName_Invalid.getValue());
            resultVo.setMsg(ResultMsg.Result_Push_CityName_Invalid.getMsg());
        }
        return resultVo;
    }

    /**
     * 判断是否是存在品牌
     *
     * @param bxwMakeName 品牌名称
     * @return
     */
    public ResultVo<Boolean> existsBxwClueMakeName(String bxwMakeName) {
        ResultVo<Boolean> booleanResultVo = new ResultVo<>();
        ResultListVo<CarMakeVo> resultListVo = carStyleBaseService.getMakeList(requestVo);
        booleanResultVo.setStatus(RetStatus.Failure.getValue());
        booleanResultVo.setData(false);
        if (resultListVo.getStatus() == RetStatus.Ok.getValue()) {
            for (CarMakeVo makeVo : resultListVo.getList()) {
                if (makeVo.getMakeName().equals(bxwMakeName)) {
                    booleanResultVo.setData(true);
                    booleanResultVo.setStatus(RetStatus.Ok.getValue());
                    booleanResultVo.setMsg("品牌：" + makeVo.getMakeName());
                    break;
                }
            }
        }
        return booleanResultVo;
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
        if (StringUtils.isNotBlank(strClueCode)) {
            if (strClueProper.equals(PublicConst.CLUEPROPER_BUY_STR)) {//如果是买家
                //数据库是否有买车数据
                strChannlCode = strClueCode;//GetListCode(resultListVo, PublicConst.CLUEPROPER_BUY);
                if (StringUtils.isNotEmpty(strChannlCode)) {
                    strResultCode = GetCode(strChannlCode);
                } else {
                    strResultCode = GetNewCode(PublicConst.CLUECODE_BUY, strChannelCode);
                }
            } else if (strClueProper.equals(PublicConst.CLUEPROPER_SELL_STR)) {//如果是卖家
                //数据库是否有卖车数据
                strChannlCode = strClueCode;//GetListCode(resultListVo, PublicConst.CLUEPROPER_SELL);
                if (StringUtils.isNotEmpty(strChannlCode)) {
                    strResultCode = GetCode(strChannlCode);
                } else {
                    strResultCode = GetNewCode(PublicConst.CLUECODE_SELL, strChannelCode);
                }
            } else if (strClueProper.equals(PublicConst.CLUEPROPER_FINANCE_STR)) {//如果是金融
                //数据库是否有金融数据
                strChannlCode = strClueCode;//GetListCode(resultListVo, PublicConst.CLUEPROPER_FINANCE);
                if (StringUtils.isNotEmpty(strChannlCode)) {
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
        if (strClueProper.equals(PublicConst.CLUEPROPER_BUY_STR)) {//如果是买家
            strResultCode = GetNewCode(PublicConst.CLUECODE_BUY, strChannelCode);
        } else if (strClueProper.equals(PublicConst.CLUEPROPER_SELL_STR)) {//如果是卖家
            strResultCode = GetNewCode(PublicConst.CLUECODE_SELL, strChannelCode);
        } else if (strClueProper.equals(PublicConst.CLUEPROPER_FINANCE_STR)) {//如果是金融
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
        if (StringUtils.isNotBlank(strChannlCode)) {
            strResultCode = strChannlCode.split("-")[2];
            Long incrementCode = Long.parseLong(strResultCode) + 1;
            strResultCode = CharUtils.paddingChar(String.valueOf(incrementCode), 11, 1, '0');//不足补0
            strResultCode = strChannlCode.split("-")[0] + "-" + strChannlCode.split("-")[1] + "-" + strResultCode;
        }
        return strResultCode;
    }

    /***
     * 如果数据库中不存在编码
     * @param strChannlCode
     * @param strChanCode
     * @return
     */
    private static String GetNewCode(String strChannlCode, String strChanCode) {
        String strResultCode = "";
        if (StringUtils.isNotBlank(strChannlCode)) {
            strResultCode = strChannlCode.split("-")[1];
            Long incrementCode = Long.parseLong(strResultCode) + 1;
            strResultCode = CharUtils.paddingChar(String.valueOf(incrementCode), 11, 1, '0');//不足补0
            strResultCode = strChanCode + "-" + strChannlCode.split("-")[0] + "-" + strResultCode;
        }
        return strResultCode;
    }


    /**
     * 验证参数的值是否为空
     */
    private class ValidateParams {
        private boolean isValidate;
        private String clusDataStr;
        private HttpServletRequest request;
        private ResultVo resultVo;
        private ClueManagVo clusData;
        private UserChannel userChannel;
        private int intClueProper;
        private int intFlag;
        private ResultListVo<ChannelPlatformRela> resultListVo1;
        private ClueManag clueManag;
        private ReceiveLog receiveLog;

        public ValidateParams(String clusDataStr, HttpServletRequest request, ResultVo resultVo, ClueManagVo clusData, UserChannel userChannel) {
            this.clusDataStr = clusDataStr;
            this.request = request;
            this.resultVo = resultVo;
            this.clusData = clusData;
            this.userChannel = userChannel;
        }

        boolean is() {
            return isValidate;
        }

        public int getIntFlag() {
            return intFlag;
        }

        public int getIntClueProper() {
            return intClueProper;
        }

        public ResultListVo<ChannelPlatformRela> getResultListVo1() {
            return resultListVo1;
        }

        public ClueManag getClueManag() {
            return clueManag;
        }

        public ReceiveLog getReceiveLog() {
            return receiveLog;
        }

        /**
         * 验证参数并赋值
         *
         * @return
         */
        public ValidateParams validate() {
            int intB = 1;
            int intS = 1;
            int intF = 1;
            intClueProper = 0;
            String strClueCode = "";
            intFlag = 0;
            resultListVo1 = userChannelService.getChannelPlatFormRela(userChannel.getId());
            if (resultListVo1 != null && resultListVo1.getList().size() > 0) {
                intFlag = 2;
            } else {
                intFlag = 1;
            }
            clueManag = new ClueManag();
            clueManag.setChannelid(userChannel.getId());
            receiveLog = new ReceiveLog();
            receiveLog.setChannelcode(userChannel.getChannelcode());
            receiveLog.setChannelname(userChannel.getChannelname());
            receiveLog.setCreatetime(new Date());
            receiveLog.setPushcontent(clusDataStr);
            String receiveUtl = request.getRequestURL().toString();
            if (StringUtils.isNotBlank(receiveUtl)) {
                receiveLog.setPushurl(receiveUtl);
            }
            if (StringUtils.isNotBlank(clusData.getMileage().toString())) {
                BigDecimal bigMiileage = new BigDecimal(clusData.getMileage().toString());
                bigMiileage.setScale(2, BigDecimal.ROUND_HALF_UP);
                clueManag.setMiileage(bigMiileage);
            } else {
                resultVo.setStatus(ResultMsg.Result_Km_NotNull.getValue());
                resultVo.setMsg(ResultMsg.Result_Km_NotNull.getMsg());
                isValidate = true;
                return this;
            }
            if (StringUtils.isNotBlank(clusData.getStyleName())) {
                clueManag.setStylename(clusData.getStyleName().trim());
            } else {
                resultVo.setStatus(ResultMsg.Result_Vehicle_Type_NotNull.getValue());
                resultVo.setMsg(ResultMsg.Result_Vehicle_Type_NotNull.getMsg());
                isValidate = true;
                return this;
            }
            if (StringUtils.isNotBlank(clusData.getCityName())) {
                clueManag.setCityname(clusData.getCityName().trim());
            } else {
                resultVo.setStatus(ResultMsg.Result_CityName_NotNull.getValue());
                resultVo.setMsg(ResultMsg.Result_CityName_NotNull.getMsg());
                isValidate = true;
                return this;
            }
            if (StringUtils.isNotBlank(clusData.getRegDate())) {
                clueManag.setRegdate(DateUtils.getRegDate(clusData.getRegDate().trim()));
            } else {
                resultVo.setStatus(ResultMsg.Result_Vehicle_Register_Date_NotNull.getValue());
                resultVo.setMsg(ResultMsg.Result_Vehicle_Register_Date_NotNull.getMsg());
                isValidate = true;
                return this;
            }
            if (StringUtils.isNotBlank(clusData.getClueType())) {
                if (clusData.getClueType().trim().equals(PublicConst.CLUEPROPER_BUY_STR)) {//买车
                    intClueProper = PublicConst.CLUEPROPER_BUY;
                    strClueCode = clueManagService.getClueCode(userChannel.getId(), PublicConst.CLUEPROPER_BUY).getData();
                    clueManag.setCluecode(GetChannlCode(strClueCode, clusData.getClueType().trim(), intB, userChannel.getChannelcode()));
                    intB++;
                } else if (clusData.getClueType().trim().equals(PublicConst.CLUEPROPER_SELL_STR)) {//卖车
                    intClueProper = PublicConst.CLUEPROPER_SELL;
                    strClueCode = clueManagService.getClueCode(userChannel.getId(), PublicConst.CLUEPROPER_SELL).getData();
                    clueManag.setCluecode(GetChannlCode(strClueCode, clusData.getClueType().trim(), intS, userChannel.getChannelcode()));
                    intS++;
                } else if (clusData.getClueType().trim().equals(PublicConst.CLUEPROPER_FINANCE_STR)) {//金融
                    intClueProper = PublicConst.CLUEPROPER_FINANCE;
                    strClueCode = clueManagService.getClueCode(userChannel.getId(), PublicConst.CLUEPROPER_FINANCE).getData();
                    clueManag.setCluecode(GetChannlCode(strClueCode, clusData.getClueType().trim(), intF, userChannel.getChannelcode()));
                    intF++;
                } else {
                    //不是上述三种则返回提示信息
                    resultVo.setStatus(ResultMsg.Result_Clue_Type_Invalid.getValue());
                    resultVo.setMsg(ResultMsg.Result_Clue_Type_Invalid.getMsg());
                    isValidate = true;
                    return this;
                }
            } else {
                resultVo.setStatus(ResultMsg.Result_Clue_Type_NotNull.getValue());
                resultVo.setMsg(ResultMsg.Result_Clue_Type_NotNull.getMsg());
                isValidate = true;
                return this;
            }
            if (StringUtils.isNotBlank(clusData.getContactsName())) {
                clueManag.setName(clusData.getContactsName());
            } else {
                resultVo.setStatus(ResultMsg.Result_Contact_Person_NotNull.getValue());
                resultVo.setMsg(ResultMsg.Result_Contact_Person_NotNull.getMsg());
                isValidate = true;
                return this;
            }
            isValidate = false;
            return this;
        }
    }

    /**
     * 组装线索所需参数
     */
    private class SetClueParams {
        private ClueManagVo clusData;
        private ValidateParams validate;
        private ClueManag clueManag;
        private int intFlag;
        private ResultListVo<ChannelPlatformRela> resultListVo1;
        private ReceiveLog receiveLog;
        private int sendType;

        public SetClueParams(ClueManagVo clusData, ValidateParams validate, int sendType) {
            this.clusData = clusData;
            this.validate = validate;
            this.sendType = sendType;
        }

        public ClueManag getClueManag() {
            return clueManag;
        }

        public int getIntFlag() {
            return intFlag;
        }

        public ResultListVo<ChannelPlatformRela> getResultListVo1() {
            return resultListVo1;
        }

        public ReceiveLog getReceiveLog() {
            return receiveLog;
        }

        public SetClueParams invoke() {
            clueManag = validate.getClueManag();
            int intClueProper = validate.getIntClueProper();
            if (sendType == PublicConst.CHANNEL_SEND_TYPE_DIRECT) {//直接分发
                clueManag.setClueproper(intClueProper);
                clueManag.setIsautodis(0);
            } else {
                //先审后发
                clueManag.setClueproper(0);
                clueManag.setIsautodis(1);
            }
            intFlag = validate.getIntFlag();
            resultListVo1 = validate.getResultListVo1();
            receiveLog = validate.getReceiveLog();
            if (StringUtils.isNotBlank(clusData.getContactsPhone())) {
                clueManag.setPhone(clusData.getContactsPhone().trim());
            }
            clueManag.setUsercode("");
            clueManag.setStoragemode(PublicConst.STORAGEMODE_Interface);
            clueManag.setPretreatment(PublicConst.PRETREATMENT);
            clueManag.setStoragetime(new Date());
            clueManag.setPushplatform("");
            clueManag.setRemark("");
            clueManag.setDisstatus(0);
            return this;
        }
    }


}

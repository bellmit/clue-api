package com.jzg.clue.api.system;

import com.alibaba.fastjson.JSON;
import com.jzg.clue.api.biz.ReciveBxwClueManagBiz;
import com.jzg.clue.service.api.UserChannelService;
import com.jzg.framework.core.vo.ResultVo;
import com.jzg.framework.core.vo.RetStatus;
import com.jzg.framework.utils.encrypt.Md5Encrypt;
import com.jzg.framework.utils.sort.QuickSort;
import com.jzg.framework.utils.string.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dara on 2017/2/23.
 */
@Component("authBiz")
public class AuthBiz {
    /**
     * APPID
     */
    @Value("${appInfo}")
    private String appKey;
    /**
     * 声明渠道服务
     */
    @Resource
    private UserChannelService userChannelService;

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthBiz.class);


    /**
     * 请求
     *
     * @param request 请求体
     * @return {status:200}
     */
    public ResultVo checkAuth(HttpServletRequest request) {
        ResultVo resultVo = new ResultVo();
        Map<String, String[]> paramMap = request.getParameterMap();
        //System.out.println(paramMap.containsKey("sign"));
        if (!paramMap.containsKey("sign")) {
            resultVo.setStatus(RetStatus.Ok.getValue());
            //resultVo.setMsg("不用验证sign");
            return resultVo;
        }
        String sign = (paramMap.containsKey("sign") && paramMap.get("sign").length > 0) ? paramMap.get("sign")[0] : "";
        if (StringUtils.isBlank(sign)) {
            resultVo.setStatus(RetStatus.Failure.getValue());
            return resultVo;
        }
        //根据channelid 判断是车商接口还是搜狐接口 并去渠道的guid
        String channelid = (paramMap.containsKey("channelid") && paramMap.get("channelid").length > 0) ? paramMap.get("channelid")[0] : "";
        ResultVo<String> resNewSign = null;
        if (channelid != "") {
            appKey = userChannelService.selectByPrimaryKey(Integer.parseInt(channelid)).getData().getGuid();
        }
        resNewSign = this.generateSign(paramMap, appKey);
        if (resNewSign.getStatus() != RetStatus.Ok.getValue()) {
            LOGGER.info("[clue-api][AuthServiceImpl][isLogin] :权限验证,请求url={},Ip={},返回结果：应用私钥appKey为空,ReturnInfo={}", request.getRequestURI() + "?" + request.getQueryString(), request.getHeader("X-Real-IP"), JSON.toJSONString(resNewSign));
            return resNewSign;
        }
        String newSign = resNewSign.getData();
        //System.out.println("sign=>" + sign);
        //System.out.println("newsign=>" + newSign);
        if (sign.toLowerCase().equals(newSign)) {
            resultVo.setStatus(RetStatus.Ok.getValue());

        } else {
            resultVo.setStatus(RetStatus.NoAuth.getValue());
            LOGGER.info("[clue-api][AuthServiceImpl][isLogin] :权限验证不通过,请求url={},Ip={},返回结果：加密后串={},ReturnInfo={}", request.getRequestURI() + "?" + request.getQueryString(), request.getHeader("X-Real-IP"), newSign, JSON.toJSONString(resultVo));
        }
        LOGGER.debug("[clue-api][AuthServiceImpl][isLogin] :权限验证,请求url={},Ip={},返回结果：加密后串={},ReturnInfo={}", request.getRequestURI() + "?" + request.getQueryString(), request.getHeader("X-Real-IP"), newSign, JSON.toJSONString(resultVo));


        return resultVo;
    }

    /**
     * 获取加密串
     *
     * @param paramMap
     * @param appKey
     * @return
     */
    public ResultVo<String> generateSign(Map<String, String[]> paramMap, String appKey) {
        ResultVo<String> res = new ResultVo<>();
//        String appId = paramMap.containsKey("appId") ? paramMap.get("appId")[0] : "";
//         = "";
//        String[] appInfoArr = appInfo.split(",");
//        for (String info : appInfoArr) {
//            String[] arr = info.split("-");
//            String id = arr[0];
//            if (appId.equals(id)) {
//                appKey = arr[1];
//                break;
//            }
//        }
        //应用私钥
        if (StringUtils.isBlank(appKey)) {
            res.setStatus(RetStatus.InValid.getValue());

            return res;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        Set<String> keySet = paramMap.keySet();
        String[] keyArr = keySet.toArray(new String[]{});
        QuickSort.sort(keyArr, 0, keyArr.length - 1); // 获取所有参数名，并排序
        for (String paramKey : keyArr) {
            if (!"sign".equals(paramKey)) {
                stringBuffer.append(paramKey);
                stringBuffer.append("=");
                stringBuffer.append(paramMap.get(paramKey)[0]);
            }
        }
//        stringBuffer.append("appKey=");
        stringBuffer.append(appKey.toLowerCase());
        String paramStr = stringBuffer.toString().toLowerCase();
        // System.out.println("paramStr=>" + paramStr);
        String newSign = Md5Encrypt.toHexString(Md5Encrypt.encrypt(paramStr, "UTF-8"));
        res.setStatus(RetStatus.Ok.getValue());
        res.setData(newSign);
        return res;
    }

}

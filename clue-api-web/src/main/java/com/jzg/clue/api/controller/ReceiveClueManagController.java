package com.jzg.clue.api.controller;


import com.alibaba.fastjson.JSON;
import com.jzg.clue.api.biz.ReciveClueManagBiz;
import com.jzg.framework.core.vo.ResultVo;
import com.jzg.framework.exception.ExceptionHandling;
import com.jzg.framework.utils.sort.QuickSort;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * Created by JZG on 2017/4/21.
 */
@Controller
@RequestMapping("/Interface")
public class ReceiveClueManagController {

    /**
     * logger
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ReceiveClueManagController.class);

    /**
     * reciveClueManagBiz 引用
     */
    @Resource
    private ReciveClueManagBiz reciveClueManagBiz;


    /**
     * 同步接受线索接口
     *
     * @param ClusData json串
     * @param request
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "JZGReceivingClues", method = RequestMethod.POST)
    public ResultVo<Long> JZGReceivingClues(String ClusData, HttpServletRequest request) {
        //LOGGER.info("[clue-api][ReceiveClueManagController][JZGReceivingClues] :同步接受线索接口 " + "传入参数：ClusData={},Ip={}", ClusData,getIpAddr(request));
        ResultVo resultVo = reciveClueManagBiz.insertReceiveClueManag(ClusData, request);
        LOGGER.info("[clue-api][ReceiveClueManagController][JZGReceivingClues] :同步接受线索接口 请求url={},传入参数：ClusData={},Ip={},返回结果：ReturnInfo={}", getRequestUrl(request), ClusData, getIpAddr(request), JSON.toJSONString(resultVo));

        return resultVo;
    }

    /***
     * 同步接受微车线索接口
     * @param ClusData
     * @param request
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "JZGReceivingWeiCheClues", method = RequestMethod.POST)
    public ResultVo<Long> JZGReceivingWeiCheClues(String ClusData, HttpServletRequest request) {
        //LOGGER.info("[clue-api][ReceiveClueManagController][JZGReceivingWeiCheClues] :同步接受微车网线索接口 " + "传入参数：ClusData={}", ClusData);
        ResultVo resultVo = reciveClueManagBiz.insertReceiveWeiCheClueManag(ClusData, request);
        LOGGER.info("[clue-api][ReceiveClueManagController][JZGReceivingWeiCheClues] :同步接受微车网线索接口 传入参数：ClusData={},Ip={},返回结果：ReturnInfo={}", ClusData, getIpAddr(request), JSON.toJSONString(resultVo));
        return resultVo;
    }


    /***
     * 收集线索接口 到MQ
     * @param ClusData
     * @param request
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "JZGReceivingCluesToMQ", method = RequestMethod.POST)
    public ResultVo<Long> JZGReceivingCluesToMQ(String ClusData, HttpServletRequest request) {
        //LOGGER.info("[clue-api][ReceiveClueManagController][JZGReceivingCluesToMQ] :异步接受线索接口" + "传入参数：ClusData={}", ClusData);
        ResultVo resultVo = reciveClueManagBiz.insertReceiveClueManagToMQ(ClusData, request);
        LOGGER.info("[clue-api][ReceiveClueManagController][JZGReceivingCluesToMQ] :异步接受线索接口 传入参数：ClusData={},Ip={},返回结果：ReturnInfo={}", ClusData, getIpAddr(request), JSON.toJSONString(resultVo));

        return resultVo;
    }

    /**
     * 搜狐二手车调取接口
     *
     * @param clueid
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/JZGGetCarClueStatus", method = RequestMethod.POST)
    public ResultVo JZGGetCarClueStatus(@RequestParam(value = "clueid", required = true) String clueid, String channelid, HttpServletRequest request) {
        //LOGGER.info("[clue-api][ReceiveClueManagController][JZGGetCarClueStatus] :获取线索状态接口 " + "传入参数：clueid={}", clueid);
        ResultVo resultVo = reciveClueManagBiz.getClueManagById(Integer.parseInt(clueid), channelid);
        if (resultVo != null)
            LOGGER.info("[clue-api][ReceiveClueManagController][JZGGetCarClueStatus] :获取线索状态接口 请求url={},传入参数：clueid={},channelid={},Ip={},返回结果：ReturnInfo={}", getRequestUrl(request), clueid, channelid, getIpAddr(request), JSON.toJSONString(resultVo));
        return resultVo;

    }

    /**
     * 爱卡获取状态调取接口
     *
     * @param clueid
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/JZGGetCarClueAKStatus", method = RequestMethod.POST)
    public ResultVo JZGGetCarClueAKStatus(@RequestParam(value = "clueid", required = true) String clueid, String channelid, HttpServletRequest request) {
        ResultVo resultVo = reciveClueManagBiz.getClueManagAKById(Integer.parseInt(clueid), channelid);
        if (resultVo != null)
            LOGGER.info("[clue-api][ReceiveClueManagController][JZGGetCarClueAKStatus] :获取线索状态接口 请求url={},传入参数：clueid={},channelid={},Ip={},返回结果：ReturnInfo={}", getRequestUrl(request), clueid, channelid, getIpAddr(request), JSON.toJSONString(resultVo));
        return resultVo;
    }

    /**
     * 获取二手车之家线索
     *
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/CarEstimate/queryData", method = RequestMethod.GET)
    public ResultVo carEstimateQueryData(HttpServletRequest request) {
        ResultVo resultVo = reciveClueManagBiz.carEstimateQueryData(request);
        if (resultVo != null)
            LOGGER.info("[clue-api][ReceiveClueManagController][carEstimateQueryData] :获取二手车之家线索接口 返回结果：ReturnInfo={}", JSON.toJSONString(resultVo));
        return resultVo;
    }

    /**
     * 给二手之家线索回传状态
     *
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/CarEstimate/updateState", method = RequestMethod.GET)
    public ResultVo carEstimateUpdateState() {
        ResultVo resultVo = reciveClueManagBiz.carEstimateUpdateState();
        if (resultVo != null)
            LOGGER.info("[clue-api][ReceiveClueManagController][carEstimateUpdateState] :获取线索状态接口 返回结果：ReturnInfo={}", JSON.toJSONString(resultVo));
        return resultVo;
    }

    /***
     * 获取ip地址
     * @param request requst
     * @return ip
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip ="";
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /***
     * 获取post的url串
     * @param request
     * @return
     */
    private static String getRequestUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        try {
            Map<String, String[]> paramMap = request.getParameterMap();
            StringBuffer sb = new StringBuffer();
            sb.append(url);
            int i = 0;
            Set<String> keySet = paramMap.keySet();
            String[] keyArr = keySet.toArray(new String[]{});
            QuickSort.sort(keyArr, 0, keyArr.length - 1); // 获取所有参数名，并排序
            for (String paramKey : keyArr) {
                if (i == 0) sb.append("?");
                else sb.append("&");
                sb.append(paramKey);
                sb.append("=");
                sb.append(paramMap.get(paramKey)[0]);
                i++;
            }
            url = sb.toString();
        } catch (Exception e) {
            LOGGER.error("[clue-api][ReceiveClueManagController][getRequestUrl] 获取post的url串，异常信息=", e);

        }
        return url;
    }
}

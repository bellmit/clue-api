package com.jzg.clue.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.jzg.clue.api.biz.ReciveBxwClueManagBiz;
import com.jzg.clue.api.biz.ReciveClueManagBiz;
import com.jzg.clue.api.util.PublicConst;
import com.jzg.clue.api.util.ResultMsg;
import com.jzg.clue.api.vo.ClueManagVo;
import com.jzg.framework.core.vo.ResultVo;
import com.jzg.framework.core.vo.RetStatus;
import com.jzg.framework.exception.ExceptionHandling;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;

/***
 * 百姓网接收线索接口
 */
@Controller
@RequestMapping("/clue")
public class ReceiveBxwClueManagController {
    /**
     * logger
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ReceiveBxwClueManagController.class);

    /**
     * reciveBxwClueManagBiz 声明
     */
    @Resource
    private ReciveBxwClueManagBiz reciveBxwClueManagBiz;


    /**
     * reciveClueManagBiz 声明
     */
    @Resource
    private ReciveClueManagBiz reciveClueManagBiz;

    /**
     * 收集百姓网线索接口
     *
     * @param clusData json类型
     * @return {status:100, data:{}}
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/bxw/receiveClues", method = RequestMethod.POST)
    public ResultVo<Long> bxwReceivingClues(String clusData, HttpServletRequest request) {
        LOGGER.debug("[clue-api][ReceiveBxwClueManagController][bxwReceivingClues] :百姓网同步接受线索接口 " + "传入参数：clusData={}", clusData);
        ResultVo resultVo = reciveBxwClueManagBiz.insertReceiveBxwClueManag(clusData, request);
        return resultVo;
    }

    /**
     * 查询渠道当天接收到的线索条数
     *
     * @param clusData json类型
     * @return {status:100, data:{}}
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/getReceiveClueCount", method = RequestMethod.POST)
    public ResultVo<Long> getReceiveClueCountByChannelCode(String clusData, HttpServletRequest request) {
        LOGGER.debug("[clue-api][ReceiveBxwClueManagController][getReceiveClueCountByChannelCode] :获取一小时内线索数 " + "传入参数：clusData={}", clusData);
        ResultVo<Long> resultVo = new ResultVo<Long>();
        Calendar c = Calendar.getInstance();
        Integer hours = c.get(Calendar.HOUR_OF_DAY);
        //晚上抓不到
        if (hours >= 23 || hours <= 7) {
            resultVo.setStatus(RetStatus.Ok.getValue());
            resultVo.setData(1L);
            resultVo.setMsg("凌晨后不抓取");
        } else {
            resultVo = reciveBxwClueManagBiz.getReceiveClueCountByChannelCode(clusData, request);
        }
        return resultVo;
    }

    /**
     * 查询线索的状态
     *
     * @param clusData json类型
     * @return {status:100, data:{}}
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/getClueStatus", method = RequestMethod.POST)
    public ResultVo getClueStatus(String clusData) {
        ResultVo resultVo = new ResultVo();
        if (clusData != null) {
            try {
                clusData = URLDecoder.decode(clusData, PublicConst.BASE_ENCODING);
                JSONObject json = (JSONObject) JSONObject.parse(clusData);
                ClueManagVo clueData = json.toJavaObject(ClueManagVo.class);
                if (clueData != null) {
                    resultVo = reciveClueManagBiz.getClueManagById(Integer.parseInt(clueData.getClueId().toString()));
                }
            } catch (Exception e) {
                LOGGER.error("[clue-api][ReceiveBxwClueManagController][getClueStatus] :获取线索状态" + "传入参数：clusData={} ,异常信息=", clusData, e);
            }
        }
        return resultVo;
    }


}

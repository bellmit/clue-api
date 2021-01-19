package com.jzg.clue.api.controller;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.jzg.clue.api.biz.ReciveClueManagBiz;
import com.jzg.clue.api.biz.SendClueManagBiz;
import com.jzg.clue.service.dto.ClueManagCityModel;
import com.jzg.framework.core.vo.ResultPageVo;
import com.jzg.framework.core.vo.ResultVo;
import com.jzg.framework.exception.ExceptionHandling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by JZG on 2017/4/24.
 */
@Controller
@RequestMapping("/clue")
public class SendClueManagController {
    /**
     * logger日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SendClueManagController.class);

    /***
     * sendClueManagBiz
     */
    @Resource
    private SendClueManagBiz sendClueManagBiz;

    /**
     * 第三方车商获取线索列表接口
     *
     * @param pageNo
     * @param pageSize
     * @param sort      排序 1 按时间排序 desc  2 按公里数排序 asc
     * @param cityId    城市id
     * @param timestamp 时间戳
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/getCluePageList", method = RequestMethod.POST)
    public ResultPageVo<ClueManagCityModel> getCluePageList(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sort", required = true) Integer sort,
            @RequestParam(value = "cityId", required = true) Integer cityId,
            @RequestParam(value = "timestamp", required = true) Long timestamp) {
        ResultPageVo<ClueManagCityModel> resultList = sendClueManagBiz.getClueManagPageList(pageNo, pageSize, sort, cityId, timestamp);

        return resultList;
    }

    /**
     * 第三方车商获取线索详情对象接口
     *
     * @param clueId 线索id
     * @return
     */
    @ResponseBody
    @ExceptionHandling
    @RequestMapping(value = "/getClueById", method = RequestMethod.POST)
    public ResultVo<ClueManagCityModel> getClueById(
            @RequestParam(value = "clueId", required = true) Integer clueId) {
        ResultVo<ClueManagCityModel> resultVo = sendClueManagBiz.getClueInfoById(clueId);
        return resultVo;

    }


}

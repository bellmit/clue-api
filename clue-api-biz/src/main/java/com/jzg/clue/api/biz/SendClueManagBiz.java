package com.jzg.clue.api.biz;

import com.jzg.clue.service.api.ClueManagService;

import com.jzg.clue.service.dto.ClueManagCityModel;
import com.jzg.framework.core.vo.ResultPageVo;
import com.jzg.framework.core.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by JZG on 2017/4/24.
 */
@Component("sendClueManagBiz")
public class SendClueManagBiz {
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(SendClueManagBiz.class);

    /**
     * 第三方获取线索信息列表
     */
    @Resource
    private ClueManagService clueManagService;

    /**
     * 第三方分页获取
     *  cityid 城市id
     *  sort 默认发布时间desc排序，公里最短排序
     *  pageSize 每页记录数
     *  pageNo 第几页
     * @return ｛status:200, list:[{}]｝
     */
    public ResultPageVo<ClueManagCityModel> getClueManagPageList(Integer pageNo, Integer pageSize, Integer  sort , Integer cityid,Long timestamp) {
       return clueManagService.getClueManagPageList(pageNo, pageSize, sort , cityid,timestamp);
    }
    public ResultVo<ClueManagCityModel> getClueInfoById(Integer clueid){
        return clueManagService.getClueInfoById(clueid);
    }

}

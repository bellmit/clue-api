package com.jzg.clue.biz;

import com.alibaba.fastjson.JSON;
import com.jzg.clue.api.vo.ClueManagVo;

import java.math.BigDecimal;

/**
 * @author mush
 * @time 2017/6/14 16:49
 * @describe 所需的json数据
 */
public class TestJson {

    public static void main(String[] args) {
        ClueManagVo clueManagVo = new ClueManagVo();
        clueManagVo.setSign("198411a4-7f98-4e2a-af5b-7b7a7cb290fe");
        clueManagVo.setCityName("北京");
        clueManagVo.setStyleName("三轮车");
        clueManagVo.setRegDate("2017年6月3日");
        BigDecimal bigMiileage = new BigDecimal("1.01");
        bigMiileage.setScale(2, BigDecimal.ROUND_HALF_UP);
        clueManagVo.setMileage(bigMiileage);
        clueManagVo.setClueType("买车");
        clueManagVo.setContactsPhone("13621089749");
        clueManagVo.setContactsName("接口测试");
        String json = JSON.toJSONString(clueManagVo);
        System.out.println(json);

    }

}

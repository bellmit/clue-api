package com.jzg.clue.api.biz;


import com.alibaba.fastjson.JSON;
import com.jzg.clue.service.api.ClueManagService;

import com.jzg.clue.service.api.ClueStatusService;
import com.jzg.clue.service.dto.SalesCluePushMQModel;
import com.jzg.clue.service.model.ChannelPlatformRela;
import com.jzg.clue.service.model.ClueManag;
import com.jzg.framework.core.vo.ResultListVo;
import com.jzg.framework.utils.json.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by dougp on 2017/4/11.
 */
@Component("cluePushMqBiz")
public class CluePushMqBiz {
    /***
     *Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CluePushMqBiz.class);

    /**
     * clueSendBiz s声明
     */
    @Resource
    private ClueSendBiz clueSendBiz;

    /***
     * clueManagService
     */
    @Resource
    private ClueManagService clueManagService;

    /**
     * /队列名称
     */
    @Value("${MQName_CluePushPlatform}")
    private String MQName_CluePushPlatform;

    /**
     * 直接到队列
     */
    @Value("${MQName_ThirdChannelClues}")
    private String MQName_ThirdChannelClues;

    /**
     * clueStatusService 声明
     */
    @Resource
    private ClueStatusService clueStatusService;

    /**
     * 获取对应的线索能推到那些平台
     * flag 1.导入线索数据直接分发，没有设置了平台
     * flag 2.导入线索数据直接分发，并且设置了平台
     * flag 3.洗数据关联，并且设置了平台
     *
     * @param ClueManag_id
     * @param flag
     * @param intChannelid
     * @param ChannelPlatformRelalist
     */
    public void PushClusToPlatform(int ClueManag_id, int flag, int intChannelid, List<ChannelPlatformRela> ChannelPlatformRelalist) {
        try {
            ResultListVo<SalesCluePushMQModel> list = clueManagService.getCluePushListMqByManagId(ClueManag_id, flag);
            if (list != null && list.getList().size() > 0) {
                for (SalesCluePushMQModel salesCluePushMQModel : list.getList()) {
                    //判断同渠道同手机号同平台每天限推两次
                    if (clueManagService.getCluePushPhoneCountByChannelId(intChannelid, ClueManag_id, salesCluePushMQModel.getPlatformValue()).getData() < 2) {
                        if (flag == 2 || flag == 3) {
                            //推送平台的上线数量
                            Integer uppernum = null;

                            for (ChannelPlatformRela channelPlatformRela : ChannelPlatformRelalist) {
                                if (salesCluePushMQModel.getPlatformValue() == channelPlatformRela.getPushplatform()) {
                                    uppernum = channelPlatformRela.getUppernum();
                                }
                            }
                            if (uppernum == 0) {
                                //系统自动分发
                                salesCluePushMQModel.setUserCode("admin");
                                AddPushClusToRabbitMQ(salesCluePushMQModel);
                            } else {
                                //获取一小时之间推送成功的条数
                                Integer pushcount = clueStatusService.getPushPlatformCount(salesCluePushMQModel.getPlatformValue(), intChannelid).getData();
                                if (pushcount < uppernum) {
                                    //系统自动分发
                                    salesCluePushMQModel.setUserCode("admin");
                                    AddPushClusToRabbitMQ(salesCluePushMQModel);
                                }
                            }
                        } else {
                            salesCluePushMQModel.setUserCode("admin");
                            //系统自动分发
                            AddPushClusToRabbitMQ(salesCluePushMQModel);
                        }
                    } else {
                        LOGGER.info("[clue-api][CluePushMqBiz][PushClusToPlatform] :同一渠道同一平台推送手机号超过2次，渠道id " + intChannelid + " 线索id " + ClueManag_id + " 推送平台 " + salesCluePushMQModel.getPlatformValue() + " 超过两次");
                    }
                }
            }
        } catch (Exception ex) {

            LOGGER.error("[clue-api][CluePushMqBiz][PushClusToPlatform] :线索推到那些平台" + "传入参数：ClueManag_id={},flag={},intChannelid={},ChannelPlatformRelalist={} ,异常信息=", ClueManag_id, flag, intChannelid, JSON.toJSONString(ChannelPlatformRelalist), ex);

        }
    }

    /**
     * 名称:添加推送线索到队列
     */
    public void AddPushClusToRabbitMQ(SalesCluePushMQModel salesCluePushMQModel) {
        try {
            String strJson = JSONUtils.toJsonString(salesCluePushMQModel);
            //发送消息到队列
            byte[] bytes = strJson.getBytes("UTF-8");
            clueSendBiz.sendClueNotify(MQName_CluePushPlatform, bytes);
            //返回成功信息
            LOGGER.info("[clue-api][CluePushMqBiz][AddPushClusToRabbitMQ] :添加推送线索到队列." + "传入参数：salesCluePushMQModel={}", JSON.toJSONString(salesCluePushMQModel));
//            try {
//                Thread.sleep(10000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        } catch (Exception e1) {
            LOGGER.error("[clue-api][CluePushMqBiz][AddPushClusToRabbitMQ] :添加推送线索到队列." + "传入参数：salesCluePushMQModel={},异常信息=", JSON.toJSONString(salesCluePushMQModel), e1);

        }
    }

    /**
     * 将获取的数据，直接推送到MQ
     *
     * @param clueManag
     */
    public void PushClusToRabbitMQ(ClueManag clueManag) {
        try {
            String strJson = JSONUtils.toJsonString(clueManag);
            //发送消息到队列
            byte[] bytes = strJson.getBytes("UTF-8");
            clueSendBiz.sendClueNotify(MQName_ThirdChannelClues, bytes);
            //返回成功信息
            //TODO 日志
            LOGGER.debug("[clue-api][CluePushMqBiz][PushClusToRabbitMQ] :将获取的数据，直接推送到MQ." + "传入参数：clueManag={}", JSON.toJSONString(clueManag));
        } catch (Exception e1) {
            LOGGER.error("[clue-api][CluePushMqBiz][PushClusToRabbitMQ] :将获取的数据，直接推送到MQ." + "传入参数：clueManag={},异常信息=", JSON.toJSONString(clueManag), e1);

        }
    }
}

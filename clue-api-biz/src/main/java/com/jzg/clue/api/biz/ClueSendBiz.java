package com.jzg.clue.api.biz;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @description: 线索消息发送
 * @author: JZG
 * @date: 2017/3/23 13:47
 */
@Component("clueSendBiz")
public class ClueSendBiz {

    /**
     *
     */
    @Resource
    private AmqpTemplate amqpTemplate;

    /***
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClueSendBiz.class);

    /***
     *
     * @param queueKey
     * @param object
     */
    public void sendClueNotify(String queueKey, Object object) {

        //LOGGER.info("[clue-api][ClueSendBiz][sendClueNotify] :发送队列." + "传入参数：queueKey={},object={}", queueKey, JSON.toJSONString( object));
        LOGGER.debug("[clue-api][ClueSendBiz][sendClueNotify] :发送到队列开始." + "传入参数：queueKey={},object={}", queueKey, JSON.toJSONString( object));

        try {
            //convertAndSend：将Java对象转换为消息发送到匹配Key的交换机中Exchange，由于配置了JSON转换，这里是将Java对象转换成JSON字符串的形式。
            amqpTemplate.convertAndSend(queueKey, object);
        } catch (Exception e) {
            LOGGER.error("[clue-api][ClueSendBiz][sendClueNotify] :发送到队列结束." + "传入参数：queueKey={},object={},异常信息=", queueKey, JSON.toJSONString( object), e);
        }
        LOGGER.debug("[clue-api][ClueSendBiz][sendClueNotify] :发送到队列结束." + "传入参数：queueKey={},object={}", queueKey, JSON.toJSONString( object));

    }
}

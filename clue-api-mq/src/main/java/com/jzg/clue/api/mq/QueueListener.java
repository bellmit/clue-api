package com.jzg.clue.api.mq;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @description: 消息监听器
 * @author: JZG
 * @date: 2017/3/23 19:33
 */
@Component("queueListener")
public class QueueListener implements MessageListener {

    private static final Logger logger = LogManager.getLogger(QueueListener.class);

    /**
     * 消息接收
     * @param message 消息
     */
    @Override
    public void onMessage(Message message) {
        try{
            System.out.print("**********************************************");
            System.out.print(message.toString());
        }catch(Exception e){
            logger.error(e);
        }
    }
}

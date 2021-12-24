package com.cpic.rabbitmqconsumer.service;

import org.omg.IOP.ComponentIdHelper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * author: guoxinghang
 * date: 2021/10/26
 */
@Component
@RabbitListener(queues = "my_dlx_queue")//监听队列（死信转过来的）
public class DlxReceiver {

    @RabbitHandler
    public void process(Map map) {
        System.out.println("DlxReceiver消费者收到消息  ：  " + map.toString());
    }

}

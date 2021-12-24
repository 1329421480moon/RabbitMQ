package com.cpic.rabbitmqconsumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * author: guoxinghang
 * date: 2021/10/26
 */

@Component
@RabbitListener(queues = "TestDirectQueue")//监听的队列名称
public class DirectReceiverSecond {

    @RabbitHandler
    public void getMap(Map map){
        System.out.println("第二个DirectReceiver消费者收到的消息 ："+map.toString());
    }
}

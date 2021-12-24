package com.cpic.rabbitmqconsumer.service;
 
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;
 
@Component
@RabbitListener(queues = "topic.woman")
public class TopicTotalReceiver {
     /*
     规则 topic.# 这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
      */
    @RabbitHandler
    public void process(Map testMessage) {
        System.out.println("TopicTotalReceiver消费者收到消息  : " + testMessage.toString());
    }
}
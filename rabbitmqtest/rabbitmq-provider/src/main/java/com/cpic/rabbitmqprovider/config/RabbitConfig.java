package com.cpic.rabbitmqprovider.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 先从总体的情况分析，推送消息存在四种情况：
 *
 * ①消息推送到server，但是在server里找不到交换机
 * ②消息推送到server，找到交换机了，但是没找到队列
 * ③消息推送到sever，交换机和队列啥都没找到
 * ④消息推送成功
 */
@Configuration
public class RabbitConfig {

    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);


        rabbitTemplate.setConfirmCallback((correlationData, b, s) -> {
            System.out.println("ConfirmCallback     相关数据："+correlationData);
            System.out.println("ConfirmCallback     确认情况："+b);
            System.out.println("ConfirmCallback     原因："+s);
        });

        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            //default void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.out.println("ReturnCallback      交换机：" + returnedMessage.getExchange());
            System.out.println("ReturnCallback      返回消息：" + returnedMessage.getMessage());
            System.out.println("ReturnCallback      路由键：" + returnedMessage.getRoutingKey());
            System.out.println("ReturnCallback      回应消息：" + returnedMessage.getReplyText());
            System.out.println("ReturnCallback      回应代码：" + returnedMessage.getReplyCode());
        });

        return rabbitTemplate;
    }
}

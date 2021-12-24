package com.cpic.rabbitmqprovider.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author: guoxinghang
 * date: 2021/10/26
 */
//死信队列
@Configuration
public class DLXRabbitConfig {

    //定义定向交换机中的持久化死信队列
    @Bean
    public Queue myDlxQueue() {
//        Queue queue = new Queue("name");

        return QueueBuilder.durable("my_dlx_queue").build();
    }

    //定义广播类型交换机
    @Bean
    public DirectExchange myDlxExchange() {
//        DirectExchange directExchange = new DirectExchange("name");

        return ExchangeBuilder.directExchange("my_dlx_exchange").build();
    }

    @Bean
    public Binding dlxExchangeToQueue() {
        //绑定路由键 my_ttl_dlx_key，可以将过期的消息转移到 my_dlx_queue队列
        return BindingBuilder.bind(myDlxQueue()).to(myDlxExchange()).with("my_ttl_dlx_key");
    }


    //定义过期队列及其属性
    @Bean
    public Queue myTtlDlxQueue() {
        //投递到该队列的消息如果没有消费都将在6秒之后被投递到死信交换机
        //设置当消息过期后投递到对应的死信交换机
        return QueueBuilder.durable("my_ttl_dlx_queue").ttl(6000).deadLetterExchange("my_dlx_exchange").build();
    }

    //定义定向交换机 根据不同的路由key投递消息
    @Bean
    public DirectExchange myNormalExchange() {
        return ExchangeBuilder.directExchange("my_normal_exchange").build();
    }


    @Bean
    public Binding normal_exchange_to_queue(@Qualifier("myTtlDlxQueue") Queue myTtlDlxQueue,
                                            @Qualifier("myNormalExchange") DirectExchange myNormalExchange) {
        return BindingBuilder.bind(myTtlDlxQueue).to(myNormalExchange).with("my_ttl_dlx_key");
    }

}

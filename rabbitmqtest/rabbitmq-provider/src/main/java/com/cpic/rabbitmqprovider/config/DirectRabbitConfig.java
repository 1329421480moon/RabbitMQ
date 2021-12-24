package com.cpic.rabbitmqprovider.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Direct Exchange
 * 直连型交换机，根据消息携带的路由键将消息投递给对应队列。
 * 大致流程，有一个队列绑定到一个直连交换机上，同时赋予一个路由键 routing key 。
 * 然后当一个消息携带着路由值为X，这个消息通过生产者发送给交换机时，交换机就会根据这个路由值X去寻找绑定值也是X的队列。
 */

@Configuration
public class DirectRabbitConfig {

    //队列 起名：TestDirectQueue
    @Bean
    public Queue TestDirectQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("TestDirectQueue", true);
        //.durable()队列名称设置队列名称  .ttl()设置失效时间单位毫秒
        //设置消息的有效期：一种是通过队列属性x-message-ttl设置，投递到该队列中的所有消息都有相同的过期时间
        //return QueueBuilder.durable("TestDirectQueue").ttl(8000).build();
        //设置消息的有效期: 另一种是通过消息属性expiration设置消息本身的有效期
        //return QueueBuilder.durable("TestDirectQueue").expires(8000).build();
    }

    //Direct交换机 起名：TestDirectExchange
    @Bean
    DirectExchange TestDirectExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        //return ExchangeBuilder.directExchange("TestDirectExchange").durable(true).build();
        return new DirectExchange("TestDirectExchange", true, false);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(TestDirectQueue()).to(TestDirectExchange()).with("TestDirectRouting");
    }

    //无队列绑定，用于测试rabbitmqTemplate 的回调函数
    @Bean
    DirectExchange lonelyDirectExchange() {
        return new DirectExchange("lonelyDirectExchange");
    }


}
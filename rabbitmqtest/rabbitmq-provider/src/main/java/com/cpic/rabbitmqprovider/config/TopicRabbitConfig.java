package com.cpic.rabbitmqprovider.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Topic Exchange
 * 主题交换机，这个交换机其实跟直连交换机流程差不多，但是它的特点就是在它的路由键和绑定键之间是有规则的。
 * 简单地介绍下规则：
 *  * (星号) 用来表示一个单词 (必须出现的)
 *  # (井号) 用来表示任意数量（零个或多个）单词
 * 通配的绑定键是跟队列进行绑定的，举个小例子
 * 队列Q1 绑定键为 *.TT.*          队列Q2绑定键为  TT.#
 * 如果一条消息携带的路由键为 A.TT.B，那么队列Q1将会收到；
 * 如果一条消息携带的路由键为TT.AA.BB，那么队列Q2将会收到；
 */

/**
 * 创建DirectRabbitConfig.java（消费者单纯的使用，其实可以不用添加这个配置，
 * 直接建后面的监听就好，使用注解来让监听器监听对应的队列即可。
 * 配置上了的话，其实消费者也是生成者的身份，也能推送该消息。）
 */
@Configuration
public class TopicRabbitConfig {
    //绑定键
    public final static String man = "topic.man";
    public final static String woman = "topic.woman";

    @Bean
    public Queue firstQueue() {
        return new Queue(TopicRabbitConfig.man);
    }

    @Bean
    public Queue secondQueue() {
        return new Queue(TopicRabbitConfig.woman);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("topicExchange");
    }


    //将firstQueue和topicExchange绑定,而且绑定的键值为topic.man
    //这样只要是消息携带的路由键是topic.man,才会分发到该队列
    @Bean
    Binding bindingExchangeMessage() {
        return BindingBuilder.bind(firstQueue()).to(exchange()).with(man);
    }

    //将secondQueue和topicExchange绑定,而且绑定的键值为用上通配路由键规则topic.#
    // 这样只要是消息携带的路由键是以topic.开头,都会分发到该队列
    // （例子中，会分发到 topic.man 和 topic.woman 队列，也是因为topic.man 和topic.woman 绑到了同一个交换机了）
    @Bean
    Binding bindingExchangeMessage2() {
        return BindingBuilder.bind(secondQueue()).to(exchange()).with("topic.#");
    }

}
package com.cpic.rabbitmqprovider.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * author: guoxinghang
 * date: 2021/10/26
 */

@RestController
public class ReturnCallbackController {

    @Autowired
    RabbitTemplate rabbitTemplate;  //使用RabbitTemplate,这提供了接收/发送等等方法

    //①消息推送到server，但是在server里找不到交换机
    //写个测试接口，把消息推送到名为‘non-existent-exchange’的交换机上（这个交换机是没有创建没有配置的）：

    //ConfirmCallback     相关数据：null
    //ConfirmCallback     确认情况：false
    //ConfirmCallback     原因：channel error; protocol method: #method<channel.close>
    // (reply-code=404, reply-text=NOT_FOUND - no exchange 'non-existent-exchange' in vhost '/', class-id=60, method-id=40)

    //结论： ①这种情况触发的是 ConfirmCallback 回调函数。
    @GetMapping("/TestMessageAck")
    public String TestMessageAck() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: non-existent-exchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("non-existent-exchange", "TestDirectRouting", map);
        return "ok";
    }

    //②消息推送到server，找到交换机了，但是没找到队列
    //这种测试需要新增一个交换机，但是不给这个交换机绑定队列，我来简单地在DirectRabitConfig里面新增一个直连交换机，
    //名叫‘lonelyDirectExchange’，但没给它做任何绑定配置操作：
    //    @Bean
    //    DirectExchange lonelyDirectExchange() {
    //        return new DirectExchange("lonelyDirectExchange");
    //    }
    //然后写个测试接口，把消息推送到名为‘lonelyDirectExchange’的交换机上（这个交换机是没有任何队列配置的）：

    //ConfirmCallback     相关数据：null
    //ConfirmCallback     确认情况：true
    //ConfirmCallback     原因：null
    //ReturnCallback      交换机：lonelyDirectExchange
    //ReturnCallback      返回消息：(Body:'{createTime=2021-10-26 10:15:05, messageId=98b967f2-7d71-4781-a83f-517b7cab8d6a, messageData=message: lonelyDirectExchange test message }' MessageProperties [headers={}, contentType=application/x-java-serialized-object, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0])
    //ReturnCallback      路由键：TestDirectRouting
    //ReturnCallback      回应消息：NO_ROUTE
    //ReturnCallback      回应代码：312

    //可以看到这种情况，两个函数都被调用了；
    //这种情况下，消息是推送成功到服务器了的，所以ConfirmCallback对消息确认情况是true；
    //而在RetrunCallback回调函数的打印参数里面可以看到，消息是推送到了交换机成功了，但是在路由分发给队列的时候，找不到队列，所以报了错误 NO_ROUTE 。
    //  结论：②这种情况触发的是 ConfirmCallback和RetrunCallback两个回调函数。
    @GetMapping("/TestMessageAck2")
    public String TestMessageAck2() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: lonelyDirectExchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("lonelyDirectExchange", "TestDirectRouting", map);
        return "ok";
    }

    //③消息推送到sever，交换机和队列啥都没找到
    //这种情况其实一看就觉得跟①很像，没错 ，③和①情况回调是一致的，所以不做结果说明了。
    //  结论： ③这种情况触发的是 ConfirmCallback 回调函数。


    //④消息推送成功
    //那么测试下，按照正常调用之前消息推送的接口就行，就调用下 /sendFanoutMessage接口，可以看到控制台输出：
    //ConfirmCallback:     相关数据：null
    //ConfirmCallback:     确认情况：true
    //ConfirmCallback:     原因：null
    //结论： ④这种情况触发的是 ConfirmCallback 回调函数。
}

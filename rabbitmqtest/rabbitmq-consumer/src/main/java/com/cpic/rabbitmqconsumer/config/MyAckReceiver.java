package com.cpic.rabbitmqconsumer.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
 
@Component
//消息接收处理类
public class MyAckReceiver implements ChannelAwareMessageListener {

    /*
      MyAckReceiver  messageId:37ffab58-b84e-48b3-9aa0-0b42ba16898f  messageData:test message, hello!  createTime:2021-10-26 10:29:29
      消费的主题消息来自：TestDirectQueue
      注意：因为设置的是直连队列(TestDirectQueue),它在此项目中有多个监听，所以会轮询，访问多次就能看到了
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        //当前消息数据的唯一 Id
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //因为传递消息的时候用的map传递,所以将Map从Message内取出需要做些处理
            String msg = message.toString();
            String[] msgArray = msg.split("'");//可以点进Message里面看源码,单引号直接的数据就是我们的map消息数据
            //(Body:'
            //{createTime=2021-10-26 11:29:10, messageId=17d9e9ff-5b13-4931-955d-b715994f09c9, messageData=test message, hello!}'
            //MessageProperties [headers={spring_listener_return_correlation=05c15daa-e51d-4e9e-9ee4-151804b7da46}, contentType=application/x-java-serialized-object, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=TestDirectExchange, receivedRoutingKey=TestDirectRouting, deliveryTag=1, consumerTag=amq.ctag-P3HOCWFLJM-gP7ffc0ODWg, consumerQueue=TestDirectQueue]
            // )
            Map<String, String> msgMap = mapStringToMap(msgArray[1].trim(),3);
            String messageId=msgMap.get("messageId");
            String messageData=msgMap.get("messageData");
            String createTime=msgMap.get("createTime");
           //单个队列时：
            System.out.println("  MyAckReceiver  messageId:"+messageId+"  messageData:"+messageData+"  createTime:"+createTime);
            System.out.println("消费的主题消息来自："+message.getMessageProperties().getConsumerQueue());
           /*
           //设置了多个队列手动接收，需判断队列的名称

            if ("TestDirectQueue".equals(message.getMessageProperties().getConsumerQueue())){
                System.out.println("消费的消息来自的队列名为："+message.getMessageProperties().getConsumerQueue());
                System.out.println("消息成功消费到  messageId:"+messageId+"  messageData:"+messageData+"  createTime:"+createTime);
                System.out.println("执行TestDirectQueue中的消息的业务处理流程......");

            }

            if ("fanout.A".equals(message.getMessageProperties().getConsumerQueue())){
                System.out.println("消费的消息来自的队列名为："+message.getMessageProperties().getConsumerQueue());
                System.out.println("消息成功消费到  messageId:"+messageId+"  messageData:"+messageData+"  createTime:"+createTime);
                System.out.println("执行fanout.A中的消息的业务处理流程......");

            }*/
            channel.basicAck(deliveryTag, true); //第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
//			channel.basicReject(deliveryTag, true);//第二个参数，true会重新放回队列，所以需要自己根据业务逻辑判断什么时候使用拒绝
        } catch (Exception e) {
            channel.basicReject(deliveryTag, false);
            e.printStackTrace();
        }
    }
 
     //{key=value,key=value,key=value} 格式转换成map
    private Map<String, String> mapStringToMap(String str,int entryNum ) {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",",entryNum);
        Map<String, String> map = new HashMap<String, String>();
        for (String string : strs) {
            String key = string.split("=")[0].trim();
            String value = string.split("=")[1];
            map.put(key, value);
        }
        return map;
    }
}
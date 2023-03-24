package com.lhc.rabbitmq.unit02;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * @Author: lhc
 * @Date: 2023/2/7 19:31
 * @ClassName: 这是一个工作线程，相当于之前的消费者
 */
public class Worker01 {
    
	//队列名称
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {

        //从工具类中获取channel
        Channel channel = RabbitMqUtils.getChannel();

        //推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedMessage = new String(delivery.getBody());
            System.out.println("接收到消息:" + receivedMessage);
        };
        
        //取消消费的一个回调接口 
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        };

        System.out.println("C2消费者启动，等待接收消息.................. ");
        //接收消息
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);

    }
}
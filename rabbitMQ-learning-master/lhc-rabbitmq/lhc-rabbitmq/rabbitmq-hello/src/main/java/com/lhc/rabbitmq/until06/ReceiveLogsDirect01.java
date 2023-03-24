package com.lhc.rabbitmq.until06;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * @Author: lhc
 * @Date: 2023/2/8 17:17
 * @ClassName: 消费者01（绑定disk队列，routingKey 为 error）
 */
public class ReceiveLogsDirect01 {

    //定义交换机名称
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        //声明一个 direct交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        //队列声明
        String queueName = "disk";
        channel.queueDeclare(queueName, false, false, false, null);
        //队列绑定
        //routingKey 为 error
        channel.queueBind(queueName, EXCHANGE_NAME, "error");
        System.out.println("等待接收消息...");

        //如何处理消息 回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            message = "接收绑定键:" + delivery.getEnvelope().getRoutingKey() + " ,消息:" + message;
            System.out.println("error 消息已经接收：\n" + message);
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }

}

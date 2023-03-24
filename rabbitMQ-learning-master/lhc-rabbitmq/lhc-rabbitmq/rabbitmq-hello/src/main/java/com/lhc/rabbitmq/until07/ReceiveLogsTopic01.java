package com.lhc.rabbitmq.until07;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;

/**
 * @Author: lhc
 * @Date: 2023/2/8 19:49
 * @ClassName: 消费者：接收消息
 */
public class ReceiveLogsTopic01 {

    //交换机名称
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //声明 Q1 队列
        String queueName = "Q1";
        channel.queueDeclare(queueName, false, false, false, null);
        //绑定
        channel.queueBind(queueName,EXCHANGE_NAME,"*.orange.*");
        System.out.println("等待接收消息........... ");

        //接收消息消费 回调接口
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" 接收队列:" + queueName + " 绑定键:" + delivery.getEnvelope().getRoutingKey() + ",消息:" + message);
        };

        //接收消息
        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});
    }

}

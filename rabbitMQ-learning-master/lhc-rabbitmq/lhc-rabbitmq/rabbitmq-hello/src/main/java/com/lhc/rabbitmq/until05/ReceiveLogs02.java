package com.lhc.rabbitmq.until05;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @Author: lhc
 * @Date: 2023/2/8 15:34
 * @ClassName:  消费者：声明队列，并且绑定交换机，同时声明队列消费者
 */
public class ReceiveLogs02 {

    //交换机名称
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        /**
         * 声明一个 exchange
         * 1.exchange 的名称
         * 2.exchange 的类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        /**
         * 声明队列
         * 生成一个临时的队列 队列的名称是随机的
         * 当消费者断开和该队列的连接时 队列自动删除
         */
        String queueName = channel.queueDeclare().getQueue();


        /**
         * 绑定交换机与队列
         *
         * 把该临时队列绑定我们的exchange ，其中 routingkey(也称之为 binding key)为空字符串
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"");

        System.out.println("等待接收消息,把接收到的消息写到文件........... ");

        //推送的消息如何进行消费的 回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            //将消息写到文件中
            String message = new String(delivery.getBody(), "UTF-8");
            File file = new File("E:\\RabbitMQ\\rabbitmq_info.txt");
            FileUtils.writeStringToFile(file,message,"UTF-8");
            System.out.println("数据写入文件成功");
        };

        //接收消息
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

    }
}

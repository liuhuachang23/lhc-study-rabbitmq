package com.lhc.rabbitmq.unit03;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

/**
 * @Author: lhc
 * @Date: 2023/2/7 22:14
 * @ClassName: 消息生产者, 消息在手动应答时是不丢失的，需要时放回队列重新消费
 */
public class Task02 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ack_queue";

    //发送消息
    public static void main(String[] args) throws Exception {

        //获取信道
        Channel channel = RabbitMqUtils.getChannel();

        //开启发布确认
        channel.confirmSelect();

        //声明队列
        boolean durable = true; //让 Queue持久化
        channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);

        //从控制台中输入消息
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入消息");
        while (scanner.hasNext()) {
            String message = scanner.next();
            //发布消息
            //MessageProperties.PERSISTENT_TEXT_PLAIN : 设置生产者消息为持久化消息(保存到磁盘中)
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
        }

    }

}

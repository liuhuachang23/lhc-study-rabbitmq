package com.lhc.rabbitmq.until05;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * @Author: lhc
 * @Date: 2023/2/8 15:39
 * @ClassName: 生产者：声明交换机，声明消息
 */
public class EmitLog {

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

        //从控制台中输入消息 发送
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入信息");
        while (sc.hasNext()) {
            String message = sc.nextLine();
            /**
             * 消息发送
             * 1) exchange 的名称
             * 2) routingKey
             * 3) 其他的参数信息
             * 4) 消息
             */
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println("生产者发出消息" + message);
        }
    }

}

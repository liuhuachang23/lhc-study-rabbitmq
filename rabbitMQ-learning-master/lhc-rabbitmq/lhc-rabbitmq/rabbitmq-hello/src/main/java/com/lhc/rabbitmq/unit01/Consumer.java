package com.lhc.rabbitmq.unit01;

import com.rabbitmq.client.*;

/**
 * @Author: lhc
 * @Date: 2023/2/7 19:31
 * @ClassName: 消费者：接收消息
 */
public class Consumer {

    //队列的名称
    private final static String QUEUE_NAME = "hello";

    //接收消息
    public static void main(String[] args) throws Exception {

        //1、创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.200.130"); //工厂IP 链接rabbitmq的队列
        factory.setUsername("admin"); //用户名
        factory.setPassword("1234567lhc"); //密码

        //2、创建连接
        Connection connection = factory.newConnection();
        //3、获取信道
        Channel channel = connection.createChannel();

        System.out.println("等待接收消息.........");

        //3）推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            //将消息体转为字符串打印出来
            String message = new String(delivery.getBody());
            System.out.println(message);
        };

        //4）消息被取消时的回调
        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息消费被中断");
        };


        /**
         * 4、消费者消费消息 - 接受消息
         *
         * 1）消费哪个队列
         * 2）消费成功之后是否要自动应答 true代表自动应答 false手动应答
         * 3）消费者未成功消费的回调
         * 4）消息被取消时的回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);


    }

}

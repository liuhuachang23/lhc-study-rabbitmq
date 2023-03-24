package com.lhc.rabbitmq.unit01;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: lhc
 * @Date: 2023/2/7 19:08
 * @ClassName: 生产者：发消息
 */
public class Producer {

    //队列名称
    public static final String QUEUE_NAME = "hello";

    //发消息
    public static void main(String[] args) throws IOException, TimeoutException {
        //1、创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.200.130"); //工厂IP 链接rabbitmq的队列
        factory.setUsername("admin"); //用户名
        factory.setPassword("1234567lhc"); //密码

        //2、channel 实现了自动 close 接口 自动关闭 不需要显示关闭
        //创建连接
        Connection connection = factory.newConnection();

        //3、获取信道
        Channel channel = connection.createChannel();

        /**
         * 4、生成一个队列
         *
         * 1）队列名称
         * 2）队列里面的消息是否 持久化(保存到磁盘) 默认false表示用完就删除(只在内存中)
         * 3）该队列是否只供一个消费者进行消费 是否进行共享 true表示可以多个消费者消费(可共享)，false只能一个消费者消费
         * 4）是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true表示自动删除
         * 5）其他参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "hello world";

        /**
         * 5、发送一个消息
         *
         * 1）发送到那个交换机（可以不指定）
         * 2）路由的key是哪个（队列名）
         * 3）其他的参数信息
         * 4）发送消息的消息体
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("消息发送完毕");
    }

}

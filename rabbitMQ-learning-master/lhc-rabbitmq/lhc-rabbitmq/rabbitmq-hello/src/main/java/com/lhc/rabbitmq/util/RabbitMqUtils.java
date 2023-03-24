package com.lhc.rabbitmq.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqUtils {
    //得到一个连接的 channel
    public static Channel getChannel() throws Exception {

        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.200.130"); //工厂IP 链接rabbitmq的队列
        factory.setUsername("admin"); //用户名
        factory.setPassword("1234567lhc"); //密码

        //2、创建连接
        Connection connection = factory.newConnection();
        //3、获取信道
        Channel channel = connection.createChannel();

        return channel;
    }
}
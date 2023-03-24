package com.lhc.rabbitmq.until08;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * @Author: lhc
 * @Date: 2023/2/9 11:52
 * @ClassName: 生产者
 */
public class Producer {
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] argv) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();

        //1、声明普通交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);

        //设置消息的 TTL 过期时间 10s （超过10s未被 消费，被绑定的死信交换机转入死信队列）
//        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();

        //该信息是用作演示队列个数限制
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", null, message.getBytes());
            System.out.println("生产者发送消息:" + message);
        }

    }

}

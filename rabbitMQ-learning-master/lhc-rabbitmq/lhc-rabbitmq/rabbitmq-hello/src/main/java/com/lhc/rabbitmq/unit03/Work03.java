package com.lhc.rabbitmq.unit03;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * @Author: lhc
 * @Date: 2023/2/7 22:22
 * @ClassName: 消费者01
 */
public class Work03 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ack_queue";

    //接收消息
    public static void main(String[] args) throws Exception {

        //获取信道
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C1 等待接收消息处理时间较短");

        //推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            //获取消息体
            String message = new String(delivery.getBody());
            //睡眠1秒（模拟 需要1秒执行业务代码）
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("接收到消息:" + message);

            //不公平分发
            //int prefetchCount = 1; //这就相当于设置consumer的信道容量，0就是无限大，1就是1，2就是2，但是分发方式还是轮询，只不过容量满了就会跳过
            //预取值
            int prefetchCount = 2;
            channel.basicQos(prefetchCount);

            /**
             * 手动应答
             * 1）消息标记 tag (消息唯一标识，标记应答那一条消息)
             * 2）是否批量应答未应答消息
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        //取消消费的一个回调接口
        CancelCallback cancelCallback = (s) -> {
            System.out.println(s + " 消费者取消消费接口回调逻辑");
        };

        //接收消息
        boolean autoAsk = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAsk, deliverCallback, cancelCallback);
    }
}

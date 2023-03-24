package com.lhc.rabbitmq.unit04;

import com.lhc.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @Author: lhc
 * @Date: 2023/2/8 13:14
 * @ClassName:
 */
public class ConfirmMessage {

    public static void main(String[] args) throws Exception {

        //单个确认发布
        publishMessageIndividually();   //发布1000个单独确认消息，耗时722ms
        //批量确认发布
        publishMessageBatch();          //发布1000个批量确认消息，耗时147ms
        //异步确认发布
        publishMessageAsync();          //发布1000个异步确认消息，耗时62ms

    }

    //发送消息个数
    public static final Integer MESSAGE_COUNT = 1000;

    /**
     * 单个确认发布
     */
    public static void publishMessageIndividually() throws Exception {

        //获取信道
        Channel channel = RabbitMqUtils.getChannel();

        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);

        //开启发布确认
        channel.confirmSelect();

        //记录开始时间
        long begin = System.currentTimeMillis();

        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {

            //发送消息
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());

            //单个消息就马上进行发布确认
            //服务端 返回true 表示发布成功
            //服务端 返回false 或 超时时间内未返回，生产者可以消息重发
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }

        }

        //记录结束时间
        long end = System.currentTimeMillis();

        //打印用时
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时" + (end - begin) + "ms");

    }

    /**
     * 批量确认发布
     */
    public static void publishMessageBatch() throws Exception {

        //获取信道
        Channel channel = RabbitMqUtils.getChannel();

        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);

        //开启发布确认
        channel.confirmSelect();

        //批量确认消息大小
        int batchSize = 100;
        //未确认消息个数
        int outstandingMessageCount = 0;

        //记录开始时间
        long begin = System.currentTimeMillis();

        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {

            //发送消息
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());

            //当发送的消息达到 batchSize个时，批量发布
            outstandingMessageCount++;
            if (outstandingMessageCount == batchSize) {
                channel.waitForConfirms();
                outstandingMessageCount = 0;
            }
        }

        //记录结束时间
        long end = System.currentTimeMillis();

        //打印用时
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息,耗时" + (end - begin) + "ms");

    }

    /**
     * 异步确认发布
     */
    public static void publishMessageAsync() throws Exception {

        //获取信道
        Channel channel = RabbitMqUtils.getChannel();

        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);

        //开启发布确认
        channel.confirmSelect();

        /**
         * 线程安全有序的一个跳表，适用于高并发的情况
         *
         * 1）轻松的将序号与消息进行关联
         * 2）轻松批量删除条目 只要给到序列号
         * 3）支持并发访问
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        /**
         * 消息确认成功 回调函数
         *
         * 1) 消息的唯一标识
         * 2) 是否为批量确认
         */
        ConfirmCallback askCallback = (deliveryTag, multiple) -> {
            //将发布确认的消息清除，剩下的就是未确认的
            if (multiple) { //如果是批量确认的
                //返回的是小于等于当前序列号的未确认消息 是一个 map
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag, true);
                //清除
                confirmed.clear();
            } else { //如果不是批量确认的
                //只清除当前序列号的消息
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认的消息：" + deliveryTag);
        };

        /**
         * 消息确认失败 回调函数
         *
         * 1) 消息的唯一标识
         * 2) 是否为批量确认
         */
        ConfirmCallback naskCallback = (deliveryTag, multiple) -> {
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认的消息：" + message + " ,未被确认，序列号: " + deliveryTag);
        };

        /**
         * 发消息之前，准备消息的监听器 监听那些消息成功了 那些消息失败了
         *
         * 1) 监听那些消息成功了
         * 2) 监听那些消息失败了
         */
        channel.addConfirmListener(askCallback, naskCallback); //异步

        //记录开始时间
        long begin = System.currentTimeMillis();

        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {

            //消息
            String message = i + "";

            /**
             * 将发送的消息 和序列号 存储到一个跳表中 （通过序列号与消息体进行一个关联）
             * 1) channel.getNextPublishSeqNo() 获取下一个消息的序列号，
             * 2) 消息内容，全部都是未确认的消息体
             */
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);

            //发送消息
            channel.basicPublish("", queueName, null, message.getBytes());

        }

        //记录结束时间
        long end = System.currentTimeMillis();

        //打印用时
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息,耗时" + (end - begin) + "ms");
    }

}

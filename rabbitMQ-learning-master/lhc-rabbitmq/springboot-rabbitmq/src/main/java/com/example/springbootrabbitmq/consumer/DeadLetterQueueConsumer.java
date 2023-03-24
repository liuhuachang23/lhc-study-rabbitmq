package com.example.springbootrabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: lhc
 * @Date: 2023/2/9 15:07
 * @ClassName: 消费者 - 死信队列
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {

    //接收信息
    @RabbitListener(queues = "QD") //监听 队列QD
    public void receiveD(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("当前时间：{},收到死信队列信息{}", new Date().toString(), msg);
    }
}

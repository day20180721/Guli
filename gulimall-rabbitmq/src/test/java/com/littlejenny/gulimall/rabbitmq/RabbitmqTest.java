package com.littlejenny.gulimall.rabbitmq;
import com.littlejenny.common.constant.RabbitmqConstants;
import com.littlejenny.gulimall.rabbitmq.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqTest {

    @Autowired
    User u1;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void send() {
        rabbitTemplate.convertAndSend(RabbitmqConstants.HANDLESTOCK_EXCHANGE,RabbitmqConstants.HANDLESTOCK_DELAY_QUEUE_KEY,u1);

    }
}
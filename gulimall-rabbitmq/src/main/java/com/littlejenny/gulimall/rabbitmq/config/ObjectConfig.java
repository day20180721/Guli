package com.littlejenny.gulimall.rabbitmq.config;

import com.littlejenny.common.constant.RabbitmqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ObjectConfig {
    @Bean
    public Queue delayStockCheck(){

        Map args = new HashMap();
        args.put("x-dead-letter-exchange",RabbitmqConstants.HANDLESTOCK_EXCHANGE);
        args.put("x-dead-letter-routing-key",RabbitmqConstants.HANDLESTOCK_REAL_QUEUE_KEY);
        //1分鐘
        args.put("x-message-ttl",60000);
        Queue queue = new Queue(RabbitmqConstants.HANDLESTOCK_DELAY_QUEUE,true,false,false,args);
        return queue;
    }
    @Bean
    public Queue realStockCheck(){
        Queue queue = new Queue(RabbitmqConstants.HANDLESTOCK_REAL_QUEUE,true,false,false,null);
        return queue;
    }
    @Bean
    public Binding bindingDelayStockCheck(){
        Binding binding = new Binding(RabbitmqConstants.HANDLESTOCK_DELAY_QUEUE, Binding.DestinationType.QUEUE,RabbitmqConstants.HANDLESTOCK_EXCHANGE,RabbitmqConstants.HANDLESTOCK_DELAY_QUEUE_KEY,null);
        return binding;
    }
    @Bean
    public Binding bindingRealStockCheck(){
        Binding binding = new Binding(RabbitmqConstants.HANDLESTOCK_REAL_QUEUE, Binding.DestinationType.QUEUE,RabbitmqConstants.HANDLESTOCK_EXCHANGE,RabbitmqConstants.HANDLESTOCK_REAL_QUEUE_KEY,null);
        return binding;
    }
    @Bean
    public Exchange exchangeStockCheck(){
        TopicExchange topicExchange = new TopicExchange(RabbitmqConstants.HANDLESTOCK_EXCHANGE,true,false);
        return topicExchange;
    }

    @Bean
    public Queue delayOrderCheck(){

        Map args = new HashMap();
        args.put("x-dead-letter-exchange",RabbitmqConstants.HANDLEORDER_EXCHANGE);
        args.put("x-dead-letter-routing-key",RabbitmqConstants.HANDLEORDER_REAL_QUEUE_KEY);
        //1分鐘
        args.put("x-message-ttl",60000);
        Queue queue = new Queue(RabbitmqConstants.HANDLEORDER_DELAY_QUEUE,true,false,false,args);
        return queue;
    }
    @Bean
    public Queue realOrderCheck(){
        Queue queue = new Queue(RabbitmqConstants.HANDLEORDER_REAL_QUEUE,true,false,false,null);
        return queue;
    }
    @Bean
    public Binding bindingDelayOrderCheck(){
        Binding binding = new Binding(RabbitmqConstants.HANDLEORDER_DELAY_QUEUE, Binding.DestinationType.QUEUE,RabbitmqConstants.HANDLEORDER_EXCHANGE,RabbitmqConstants.HANDLEORDER_DELAY_QUEUE_KEY,null);
        return binding;
    }
    @Bean
    public Binding bindingRealOrderCheck(){
        Binding binding = new Binding(RabbitmqConstants.HANDLEORDER_REAL_QUEUE, Binding.DestinationType.QUEUE,RabbitmqConstants.HANDLEORDER_EXCHANGE,RabbitmqConstants.HANDLEORDER_REAL_QUEUE_KEY,null);
        return binding;
    }
    @Bean
    public Exchange exchangeOrderCheck(){
        TopicExchange topicExchange = new TopicExchange(RabbitmqConstants.HANDLEORDER_EXCHANGE,true,false);
        return topicExchange;
    }

    @Bean
    public Queue secOrder(){
        Queue queue = new Queue(RabbitmqConstants.HANDLESECORDER_QUEUE,true,false,false,null);
        return queue;
    }
    @Bean
    public Binding bindingsecOrder(){
        Binding binding = new Binding(RabbitmqConstants.HANDLESECORDER_QUEUE, Binding.DestinationType.QUEUE,RabbitmqConstants.HANDLESECORDER_QUEUE_KEY,RabbitmqConstants.HANDLEORDER_REAL_QUEUE_KEY,null);
        return binding;
    }
}

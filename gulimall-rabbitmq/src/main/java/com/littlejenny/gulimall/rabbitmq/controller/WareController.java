package com.littlejenny.gulimall.rabbitmq.controller;

import com.littlejenny.common.constant.RabbitmqConstants;
import com.littlejenny.common.to.order.OrderTO;
import com.littlejenny.common.to.ware.WareOrderTaskDetailTO;
import com.littlejenny.common.to.ware.WareOrderTaskTO;
import com.littlejenny.common.utils.R;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WareController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostMapping("/task")
    public R task(@RequestBody WareOrderTaskTO wareOrderTaskTO){
        try {
            rabbitTemplate.convertAndSend(
                    RabbitmqConstants.HANDLEORDER_EXCHANGE,
                    RabbitmqConstants.HANDLEORDER_DELAY_QUEUE_KEY,
                    wareOrderTaskTO);
        }catch (Exception e){
            e.printStackTrace();
            //防止丟失
            //網路錯誤，需要重新嘗試或是將訊息放到SQL
            //放到SQL後過一陣子再發送
        }
        return R.ok();
    }
    @PostMapping("/taskDetail")
    public R taskDetail(@RequestBody WareOrderTaskDetailTO wareOrderTaskDetailTO){
        try {
            rabbitTemplate.convertAndSend(
                    RabbitmqConstants.HANDLESTOCK_EXCHANGE,
                    RabbitmqConstants.HANDLESTOCK_DELAY_QUEUE_KEY,
                    wareOrderTaskDetailTO);
        }catch (Exception e){
            e.printStackTrace();
        }
        return R.ok();
    }
    @PostMapping("/taskExpire")
    public R taskExpire(@RequestBody WareOrderTaskTO wareOrderTaskTO){
        try {
            rabbitTemplate.convertAndSend(
                    RabbitmqConstants.HANDLESTOCK_EXCHANGE,
                    RabbitmqConstants.HANDLEORDER_TOSTOCK_QUEUE_KEY,
                    wareOrderTaskTO);
        }catch (Exception e){
            e.printStackTrace();
        }
        return R.ok();
    }
    @PostMapping("/secOrder")
    public R secOrder(@RequestBody OrderTO OrderTO){
        try {
            rabbitTemplate.convertAndSend(
                    RabbitmqConstants.HANDLEORDER_EXCHANGE,
                    RabbitmqConstants.HANDLESECORDER_QUEUE_KEY,
                    OrderTO);
        }catch (Exception e){
            e.printStackTrace();
        }
        return R.ok();
    }
}

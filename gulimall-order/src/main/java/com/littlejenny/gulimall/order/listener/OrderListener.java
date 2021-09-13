package com.littlejenny.gulimall.order.listener;

import com.littlejenny.common.enums.order.OrderStatusEnum;
import com.littlejenny.common.to.ware.WareOrderTaskTO;
import com.littlejenny.gulimall.order.entity.OrderEntity;
import com.littlejenny.gulimall.order.feign.RabbitMqFeignService;
import com.littlejenny.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
@Slf4j
@Component
@RabbitListener(queues = "order.release.order.queue")
public class OrderListener {
    /**
     * 判斷訂單是否過期
     */
    @Autowired
    private OrderService orderService;
    @Autowired
    private RabbitMqFeignService rabbitMqFeignService;
    @Transactional
    @RabbitHandler
    public void handleOrder(WareOrderTaskTO order, Message message, Channel channel) throws IOException {
        log.info("著手處理OrderTask");
        OrderEntity entity = orderService.getBySn(order.getOrderSn());
        //有訂單但是過期
        if(entity != null){
            if(entity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){
                entity.setStatus(OrderStatusEnum.CANCLED.getCode());
                orderService.updateById(entity);
            }
            //主動將訂單被取消的事實告訴Detail，以免Detail因為某些原因提早執行，導致無法刪單
            //如果失敗就可能會造成無法刪單，目前想法就是在SQL紀錄一下
            rabbitMqFeignService.taskExpire(order);
        }else {
        //因為在製造訂單的過程中就失敗了，所以沒有訂單
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        log.info("完美處理OrderTask");
    }
}

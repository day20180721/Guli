package com.littlejenny.gulimall.ware.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.littlejenny.common.enums.order.OrderStatusEnum;
import com.littlejenny.common.to.order.OrderTO;
import com.littlejenny.common.to.ware.WareOrderTaskDetailTO;
import com.littlejenny.common.to.ware.WareOrderTaskTO;
import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.ware.dao.WareSkuDao;
import com.littlejenny.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.littlejenny.gulimall.ware.enums.OrderDetailState;
import com.littlejenny.gulimall.ware.feign.OrderFeignService;
import com.littlejenny.gulimall.ware.service.WareOrderTaskDetailService;
import com.littlejenny.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RabbitListener(queues = "stock.release.stock.queue")
public class StockListener {
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    private WareSkuDao wareSkuDao;

    @Transactional
    @RabbitHandler
    public void handleStock(WareOrderTaskDetailTO detail, Message message, Channel channel) throws IOException {
        //錯誤只有三種結果
        //第一就是在調用遠程服務Feign導致的錯誤
        //第二是Ack導致的錯誤
        //第三次Reject導致的錯誤
        log.info("著手處理handleStock");
        try {
            WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detail.getId());
            if (byId != null) {
                //如果該操作單已經使用過則不做任何事情
                if (byId.getTaskState() != OrderDetailState.LOCKED.getCode()) {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    log.info("完美處理handleStock，因為此任務細節已被消費");
                    return;
                }
                R r = orderFeignService.infoBySn(detail.getOrderSn());
                OrderTO orderTO = r.getData(new TypeReference<OrderTO>() {
                });
                if (orderTO == null) {
                    //要反轉，因為訂單創建失敗還被扣庫存
                    wareSkuDao.unlockStock(byId.getSkuId(), byId.getSkuNum(), byId.getWareId());
                    byId.setTaskState(OrderDetailState.UNLOCKED.getCode());
                    wareOrderTaskDetailService.updateById(byId);
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    log.info("完美處理handleStock，此訂單創建過程失敗");
                    return;
                } else {
                    if (orderTO.getStatus() == OrderStatusEnum.CANCLED.getCode()) {
                        //如果訂單取消了，就要反轉庫存
                        wareSkuDao.unlockStock(byId.getSkuId(), byId.getSkuNum(), byId.getWareId());
                        byId.setTaskState(OrderDetailState.UNLOCKED.getCode());
                        wareOrderTaskDetailService.updateById(byId);
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                        log.info("完美處理handleStock，此訂單已過時");
                        return;
                    }
                }
            } else {
                //沒操作就不用返回
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            log.info("居然處理handleStock失敗了!!!");
        }

    }

    /**
     * 過期的訂單需要解鎖所有項目
     */
    @Transactional
    @RabbitHandler
    public void handleOrder(WareOrderTaskTO order, Message message, Channel channel) throws IOException {
        log.info("著手處理handleOrder");
        List<WareOrderTaskDetailEntity> entitys = wareOrderTaskDetailService.listBySn(order.getOrderSn());
        if (entitys != null && entitys.size() > 0) {
            for (WareOrderTaskDetailEntity entity : entitys) {
                if (entity.getTaskState() != OrderDetailState.LOCKED.getCode()) continue;
                wareSkuDao.unlockStock(entity.getSkuId(), entity.getSkuNum(), entity.getWareId());

            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        log.info("完美處理handleOrder");
    }
}

package com.littlejenny.gulimall.order.service.impl;

import com.littlejenny.gulimall.order.entity.OrderItemEntity;
import com.littlejenny.gulimall.order.service.OrderItemService;
import com.littlejenny.gulimall.order.vo.orders.OrderItemVO;
import com.littlejenny.gulimall.order.vo.orders.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.order.dao.OrderDao;
import com.littlejenny.gulimall.order.entity.OrderEntity;
import com.littlejenny.gulimall.order.service.OrderService;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderEntity getBySn(String orderSn) {
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("order_sn",orderSn);
        OrderEntity entity = this.baseMapper.selectOne(wrapper);
        return entity;
    }
    @Autowired
    private OrderItemService orderItemService;
    @Override
    public List<OrderVO> queryPageWithItem(String pageNo, Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );
        List<OrderEntity> records = page.getRecords();
        List<OrderVO> orderVOS = records.stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            List<OrderItemEntity> items = orderItemService.getBySn(order.getOrderSn());
            if (items == null || items.size() == 0) {
                log.error("訂單", order.getOrderSn(), "為空，有異常");
            } else {
                List<OrderItemVO> itemsVO = items.stream().map(item -> {
                    OrderItemVO itemVo = new OrderItemVO();
                    BeanUtils.copyProperties(item, itemVo);
                    return itemVo;
                }).collect(Collectors.toList());
                vo.setItems(itemsVO);
            }
            return vo;
        }).collect(Collectors.toList());
        return orderVOS;
    }


}
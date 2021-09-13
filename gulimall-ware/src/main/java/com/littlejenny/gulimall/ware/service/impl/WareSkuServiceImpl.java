package com.littlejenny.gulimall.ware.service.impl;

import com.littlejenny.common.to.ware.HasStockTO;
import com.littlejenny.common.exception.ware.NoWareCanHandleSkuException;
import com.littlejenny.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.littlejenny.gulimall.ware.enums.OrderDetailState;
import com.littlejenny.gulimall.ware.feign.RabbitMqFeignService;
import com.littlejenny.gulimall.ware.service.WareOrderTaskDetailService;
import com.littlejenny.gulimall.ware.to.HasStockWareTO;
import com.littlejenny.gulimall.ware.to.SubtarctStockTO;
import com.littlejenny.gulimall.ware.to.SubtractStockDetailTO;
import lombok.extern.slf4j.Slf4j;
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

import com.littlejenny.gulimall.ware.dao.WareSkuDao;
import com.littlejenny.gulimall.ware.entity.WareSkuEntity;
import com.littlejenny.gulimall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private WareSkuDao wareSkuDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * skuId: 123
     * wareId: 2
     */
    @Autowired
    private RabbitMqFeignService rabbit;
    @Override
    public PageUtils queryPageBySkuIdWareId(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String)params.get("skuId");
        if(!StringUtils.isEmpty(skuId) && skuId.matches("^\\d*$")){
            wrapper.eq("sku_id",skuId);
        }
        String wareId = (String)params.get("wareId");
        if(!StringUtils.isEmpty(wareId) && wareId.matches("^\\d*$")){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void updateStock(Long skuId, Long wareId, Integer skuNum) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId).eq("ware_id",wareId).eq("stock",skuNum);
        WareSkuEntity entity = getOne(wrapper);
        if(entity != null){
            wareSkuDao.updateStock(skuId,wareId,skuNum);
        }else {
            WareSkuEntity e = new WareSkuEntity();
            e.setSkuId(skuId);
            e.setWareId(wareId);
            e.setStock(skuNum);
            //TODO 增加商品名稱
            save(e);
        }
    }

    @Override
    public Map<Long, HasStockTO> hasStockByIds(List<Long> skuIds) {
        //該Sku在所有倉庫中的數量總和減去被保留的數量
        Map<Long, HasStockTO> collect = skuIds.stream().map(item -> {
            Integer stock = wareSkuDao.hasStockById(item);
            HasStockTO to = new HasStockTO();
            to.setSkuId(item);
            if (stock == null) {
                to.setHasStock(false);
            } else {
                to.setHasStock(stock == 0 ? false : true);
            }
            return to;
        }).collect(Collectors.toMap(HasStockTO::getSkuId, item -> {
            return item;
        }));
        return collect;
    }
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Override
    public void lockStocks(SubtarctStockTO subtarctStockTOS) throws NoWareCanHandleSkuException {
        for (SubtractStockDetailTO stockTO : subtarctStockTOS.getDetailTOList()) {
            //查看有哪些倉庫有此貨
            List<HasStockWareTO> tos = wareSkuDao.getHasStockWareBySKuID(stockTO.getSkuId(),stockTO.getQuantity());
            if(tos == null || tos.size() == 0){
                throw new NoWareCanHandleSkuException(stockTO.getSkuId());
            }
            //TODO 預設使用第一個倉庫，應該要根據多種情況來選擇，如用戶所在地等等
            Integer integer = wareSkuDao.lockStock(stockTO.getSkuId(), stockTO.getQuantity(), tos.get(0).getWareId());
            if(integer == 0){
                log.info("庫存",stockTO.getSkuId(),"在倉庫:",tos.get(0).getWareId(),"不足");
                throw new NoWareCanHandleSkuException(stockTO.getSkuId());
            }
            WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
            wareOrderTaskDetailEntity.setWareId(tos.get(0).getWareId());
            wareOrderTaskDetailEntity.setSkuId(stockTO.getSkuId());
            wareOrderTaskDetailEntity.setSkuNum(stockTO.getQuantity());
            wareOrderTaskDetailEntity.setTaskState(OrderDetailState.LOCKED.getCode());
            wareOrderTaskDetailEntity.setOrderSn(subtarctStockTOS.getOrderSn());
            //保存到資料庫
            wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
            //保存到rabbitMQ
            try{
                rabbit.taskDetail(wareOrderTaskDetailEntity);
            }catch (Exception e){

            }
        }
    }
}
package com.littlejenny.gulimall.ware.service.impl;

import com.littlejenny.common.constant.Ware;
import com.littlejenny.gulimall.ware.entity.PurchaseDetailEntity;
import com.littlejenny.gulimall.ware.service.PurchaseDetailService;
import com.littlejenny.gulimall.ware.service.WareSkuService;
import com.littlejenny.gulimall.ware.vo.DetailResultVO;
import com.littlejenny.gulimall.ware.vo.DoneVO;
import com.littlejenny.gulimall.ware.vo.PurchaseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.common.utils.Query;

import com.littlejenny.gulimall.ware.dao.PurchaseDao;
import com.littlejenny.gulimall.ware.entity.PurchaseEntity;
import com.littlejenny.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;
    @Transactional
    @Override
    public void received(List<Long> ids) {
        if(ids == null || ids.size() == 0)return;
        ids.forEach(item -> {
            //更新採購單狀態
            PurchaseEntity entity = getById(item);
            //確認此採購單是不是已經完成了，如果是則跳過
            if(entity == null || !(entity.getStatus() == Ware.PurchaseStatus.CREATED.getCode() || entity.getStatus() == Ware.PurchaseStatus.ASSIGNED.getCode())){
                return;
            }
            entity.setUpdateTime(new Date());
            entity.setStatus(Ware.PurchaseStatus.RECEIVE.getCode());
            updateById(entity);
            //根據此採購單找尋對應之項目，而我們在一開始分配時就有先過濾掉
            //除了新建或是等待的項目
            //所以我這邊就不判定哪一些項目為不合法的，如已經正在派送、完成、失敗
            //但是最後還是篩了，怕還是會有錯誤
            QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("purchase_id", item);
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.list(wrapper);
            //更新這些項目後保存
            List<PurchaseDetailEntity> modifiedEntities = purchaseDetailEntities.stream().filter(item2->{
                return (item2.getStatus() == Ware.PurchaseDetailStatus.CREATED.getCode() || item2.getStatus() == Ware.PurchaseDetailStatus.ASSIGNED.getCode());
            }).map(dItem -> {
                dItem.setStatus(Ware.PurchaseDetailStatus.BUYING.getCode());
                return dItem;
            }).collect(Collectors.toList());
            //如果篩選完後為空就不用存了會報錯
            if(modifiedEntities != null && modifiedEntities.size() > 0)
                purchaseDetailService.updateBatchById(modifiedEntities);
        });
    }

    @Transactional
    @Override
    public void done(DoneVO vo) {
        Long purchaseID = vo.getId();
        PurchaseEntity purchaseEntity = getById(purchaseID);
        List<DetailResultVO> detailResultVOList = vo.getItems();
        boolean success = true;
        //封裝項目為儲存格式，順便判斷是不是都完成了
        for (DetailResultVO item : detailResultVOList) {
            Long itemId = item.getItemId();
            Integer status = item.getStatus();
            //更新項目狀態
            PurchaseDetailEntity detailEntity = purchaseDetailService.getById(itemId);
            detailEntity.setStatus(status);
            purchaseDetailService.updateById(detailEntity);
            if(status == Ware.PurchaseDetailStatus.HASERROR.getCode()){
                success = false;
            }else {
                //更改庫存
                Long skuId = detailEntity.getSkuId();
                Long wareId = detailEntity.getWareId();
                Integer skuNum = detailEntity.getSkuNum();

                wareSkuService.updateStock(skuId,wareId,skuNum);
            }
        }
        //更改採購單狀態
        purchaseEntity.setUpdateTime(new Date());
        if(!success){
            purchaseEntity.setStatus(Ware.PurchaseStatus.HASERROR.getCode());
        }else {
            purchaseEntity.setStatus(Ware.PurchaseStatus.FINISH.getCode());
        }
        updateById(purchaseEntity);
        //

    }

    @Transactional
    @Override
    public void merge(PurchaseVO vo) {
        Long purchaseId = vo.getPurchaseId();
        //找到所有項目
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        Long[] items = vo.getItems();
        if(items != null && items.length > 0){
            wrapper.in("id",items);
            //確認此項目是否仍然為新建或是剛分配，還沒被採購員確認購買
            wrapper.and(w ->{
                w.eq("status",Ware.PurchaseDetailStatus.ASSIGNED.getCode())
                        .or()
                        .eq("status",Ware.PurchaseDetailStatus.CREATED.getCode());
            });
        }
        List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.list(wrapper);
        //如果都選中無效的項目，則就不更改、保存那些項目
        if(purchaseDetailEntities != null && purchaseDetailEntities.size() >0){
            //如果沒有此採購單則動態生成
            if(purchaseId == null){
                PurchaseEntity purchaseEntity = new PurchaseEntity();
                purchaseEntity.setCreateTime(new Date());
                purchaseEntity.setUpdateTime(new Date());
                purchaseEntity.setStatus(Ware.PurchaseStatus.CREATED.getCode());
                save(purchaseEntity);
                purchaseId = purchaseEntity.getId();
            }else {
                //更新該採購單的更新時間
                PurchaseEntity purchaseEntity = new PurchaseEntity();
                purchaseEntity.setId(purchaseId);
                purchaseEntity.setUpdateTime(new Date());
                updateById(purchaseEntity);
            }
            //將所有項目改成已經分配以及給予採購單ID
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> modifiedentities = purchaseDetailEntities.stream().map(item -> {
                item.setPurchaseId(finalPurchaseId);
                item.setStatus(Ware.PurchaseDetailStatus.ASSIGNED.getCode());
                return item;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(modifiedentities);
        }
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceiveList(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", Ware.PurchaseStatus.CREATED.getCode())
                .or()
                .eq("status",Ware.PurchaseStatus.ASSIGNED.getCode());
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}
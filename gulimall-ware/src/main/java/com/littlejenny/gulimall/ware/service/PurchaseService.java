package com.littlejenny.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.littlejenny.common.utils.PageUtils;
import com.littlejenny.gulimall.ware.entity.PurchaseEntity;
import com.littlejenny.gulimall.ware.vo.DoneVO;
import com.littlejenny.gulimall.ware.vo.PurchaseVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:35:23
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceiveList(Map<String, Object> params);

    void merge(PurchaseVO vo);

    void received(List<Long> ids);

    void done(DoneVO vo);
}


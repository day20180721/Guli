package com.littlejenny.gulimall.product.feign;
import com.littlejenny.common.to.coupon.MemberPriceTO;
import com.littlejenny.common.to.coupon.SkufullReductionTO;
import com.littlejenny.common.to.coupon.SkuLadderTO;
import com.littlejenny.common.to.coupon.SpuBoundsTO;
import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(value = "coupon")
public interface CouponService {
    @PostMapping("/coupon/spubounds/save")
    R saveBound(@RequestBody SpuBoundsTO spuBounds);

    @PostMapping("/coupon/skuladder/save")
    R saveSkuladder(@RequestBody SkuLadderTO skuladderTO);

    @PostMapping("/coupon/skufullreduction/save")
    R saveSkufullreduction(@RequestBody SkufullReductionTO skufullreductionTO);

    @PostMapping("/coupon/memberprice/saveBatch")
    R saveMemberPrice(@RequestBody List<MemberPriceTO> memberPriceTO);
}

package com.littlejenny.gulimall.order.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Component
@FeignClient(value = "cart")
public interface CartFeignService {

    @GetMapping("/cartItems/{mID}")
    R cartItems(@PathVariable("mID")Long mId); //List<CartItemVO>
    @GetMapping("/cartItems/new")
    R cartItemsNewest();// List<CartItemVO> R.ok().setData(items)
}

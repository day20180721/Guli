package com.littlejenny.gulimall.order.feign;

import com.littlejenny.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient(value = "member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/list/{mID}")
    R attrsListByMId(@PathVariable("mID")Long mID);//List<MemberReceiveAddressEntity>
    @RequestMapping("/member/member/info/{id}")
    R memberInfo(@PathVariable("id") Long mID); //MemberEntity R.ok().put("member", member);
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R addrinfo(@PathVariable("id") Long id);//MemberReceiveAddressEntity R.ok().put("memberReceiveAddress", memberReceiveAddress);
}

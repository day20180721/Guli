package com.littlejenny.gulimall.auth.feign;

import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.auth.vo.LoginAccountVO;
import com.littlejenny.gulimall.auth.vo.RegistAccountVO;
import com.littlejenny.gulimall.auth.vo.google.GoogleUserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "member")
public interface MemberFeignService {
    @PostMapping("/member/member/regist")
    R regist(@RequestBody RegistAccountVO vo);
    @PostMapping("/member/member/oauthLogin")
    R oauthLogin(@RequestBody GoogleUserInfoVO vo);
    @PostMapping("/member/member/login")
    R login(@RequestBody LoginAccountVO vo);
}

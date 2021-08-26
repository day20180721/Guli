package com.littlejenny.gulimall.order.interceptor;
import com.alibaba.fastjson.JSON;
import com.littlejenny.common.constant.CartConstants;
import com.littlejenny.common.entity.VisitorLoginState;
import com.littlejenny.common.to.MemberEntityTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<VisitorLoginState> visitorLoginState = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object o = session.getAttribute(CartConstants.USERKEY);
        String userJson = JSON.toJSONString(o);
        MemberEntityTO user = JSON.parseObject(userJson, MemberEntityTO.class);
        VisitorLoginState state = new VisitorLoginState();
        if(user != null){
            state.setUserId(user.getId().toString());
            visitorLoginState.set(state);
            return true;
        }
        log.error("The request is not sent from user");
        return false;
    }
}

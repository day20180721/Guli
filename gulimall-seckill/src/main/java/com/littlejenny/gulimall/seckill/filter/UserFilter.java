package com.littlejenny.gulimall.seckill.filter;

import com.alibaba.fastjson.JSON;
import com.littlejenny.common.constant.CartConstants;
import com.littlejenny.common.entity.VisitorLoginState;
import com.littlejenny.common.to.member.MemberEntityTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
public class UserFilter implements Filter {
    public static ThreadLocal<VisitorLoginState> visitorLoginState = new ThreadLocal<>();
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequestrequest =(HttpServletRequest) request;

        String requestURI = httpServletRequestrequest.getRequestURI();
        System.out.println(requestURI);
        boolean match = new AntPathMatcher().match("/currentEvent", requestURI);
        boolean match1 = new AntPathMatcher().match("/isOnline/**", requestURI);
        if(match || match1){
            System.out.println("放行調用");
            chain.doFilter(request,response);
            return;
        }


        HttpSession session = httpServletRequestrequest.getSession();
        Object o = session.getAttribute(CartConstants.USERKEY);
        String userJson = JSON.toJSONString(o);
        MemberEntityTO user = JSON.parseObject(userJson, MemberEntityTO.class);
        VisitorLoginState state = new VisitorLoginState();
        if(user != null){
            state.setUserId(user.getId().toString());
            visitorLoginState.set(state);
            chain.doFilter(request,response);
        }else {
            session.setAttribute("msg","請先登入會員");
            log.error("The request is not sent from user and back to auth service");
            HttpServletResponse httpServletResponse = (HttpServletResponse)response;
            httpServletResponse.sendRedirect("http://auth.gulimall.com/login.html");
        }
    }
}

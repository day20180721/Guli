package com.littlejenny.gulimall.cart.interceptor;
import com.alibaba.fastjson.JSON;
import com.littlejenny.common.to.member.MemberEntityTO;
import com.littlejenny.common.constant.CartConstants;
import com.littlejenny.common.entity.VisitorLoginState;
import com.littlejenny.common.utils.WebUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public class VisitorLoginStateInterceptor implements HandlerInterceptor {
    public static ThreadLocal<VisitorLoginState> visitorLoginState = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        /**
         * 判斷在Auth 緩存中是否有會員信息
         * 如果沒有則給他一個visitorId、以及設定firstLogin，用以外postHandle向瀏覽器發送cookie
         */
        Object o = session.getAttribute(CartConstants.USERKEY);
        String userJson = JSON.toJSONString(o);

        MemberEntityTO user = JSON.parseObject(userJson, MemberEntityTO.class);
        VisitorLoginState state = new VisitorLoginState();
        if(user != null){
            state.setUserId(user.getId().toString());
            Cookie cookie = WebUtils.getCookie(request, CartConstants.VISITOR_COOKIE_NAME);
            if(cookie != null){
                state.setVisitorId(cookie.getValue());
            }
        }else{
            Cookie cookie = WebUtils.getCookie(request, CartConstants.VISITOR_COOKIE_NAME);
            if(cookie == null){
                //請求沒有cookie，要給他一個新的
                String uuid = UUID.randomUUID().toString();
                state.setVisitorId(uuid);
                state.setFirstLogin(true);
            }else {
                state.setVisitorId(cookie.getValue());
            }
        }
        visitorLoginState.set(state);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        VisitorLoginState state = visitorLoginState.get();
        if(state.getFirstLogin()){
            Cookie cookie = new Cookie(CartConstants.VISITOR_COOKIE_NAME,state.getVisitorId());
            cookie.setMaxAge(CartConstants.VISITOR_COOKIE_EXPIRE);
            response.addCookie(cookie);
        }
    }
}

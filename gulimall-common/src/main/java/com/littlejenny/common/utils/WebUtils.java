package com.littlejenny.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class WebUtils {
    public static Cookie getCookie(HttpServletRequest request,String target){
        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals(target)){
                return cookie;
            }
        }
        return null;
    }
}

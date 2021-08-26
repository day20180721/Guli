package com.littlejenny.common.constant;

public class CartConstants {
    public static String USERKEY = "user";
    public static String VISITOR_COOKIE_NAME = "user-key";
    public static Integer VISITOR_COOKIE_EXPIRE = 60 * 60 * 24 * 30;
    //Redis會存為PREFIX 加上 VisitorLoginStateID ->有UserID就存User，沒有則Visitor
    public static String CART_PREFIX = "Gulimall:Cart:";
}

package com.littlejenny.gulimall.auth.utils;

import java.util.Map;

public class URLs {
    public static String buildUrl(String url, Map<String,String> map){
        StringBuffer buffer = new StringBuffer();
        buffer.append(url);
        buffer.append("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            buffer.append(entry.getKey() + "="+entry.getValue()+"&");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }
}

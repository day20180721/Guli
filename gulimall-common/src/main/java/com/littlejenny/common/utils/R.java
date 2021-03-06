/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.littlejenny.common.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	public R setData(Object obj){
		return this.put("data",obj);
	}
	public <T> T getValue(String key,TypeReference<T> type){
		Object data = this.get(key);
		String s = JSON.toJSONString(data);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			T t = objectMapper.readValue(s, type);
			return t;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public <T> T getData(TypeReference<T> type){
		return getValue("data",type);
	}
	public R() {
		put("code", 0);
		put("msg", "success");
	}

	public Integer getCode(){
		return (Integer)get("code");
	}
	public Boolean isSuccess(){
		return get("code") != null && (Integer)get("code") == 0;
	}
	public String getMsg(){return (String)get("msg");}
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}

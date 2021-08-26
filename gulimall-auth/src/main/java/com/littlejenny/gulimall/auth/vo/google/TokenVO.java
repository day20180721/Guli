/**
 * Copyright 2021 bejson.com
 */
package com.littlejenny.gulimall.auth.vo.google;

import lombok.Data;
import lombok.ToString;

/**
 * Auto-generated: 2021-08-19 12:45:39
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@ToString
public class TokenVO {

    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
package com.littlejenny.common.entity;

import lombok.Data;

@Data
public class VisitorLoginState {
    private String userId;
    private String visitorId;
    private Boolean firstLogin = false;
}

package com.littlejenny.gulimall.auth.constants;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AuthConstants {
    public static String REDIS_PREFIX = "sms_";
    public static long SMS_TIMEOUT = 600000L;
    public static String GOOGLE_API_CERTIFICATION_PAGE_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    public static String GOOGLE_GET_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static String REDIRECT_LONIN_URL = "http://auth.gulimall.com/oauth/google/loginCallback";
    public static String GOOGLE_API_CERTIFICATION_INFO_URL = "https://www.googleapis.com/auth/userinfo.profile";
    public static String GOOGLE_API_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

    public static String USERKEY = "user";

    @Value("${googleapi.clientId}")
    private String client_id;
    @Value("${googleapi.clientSecret}")
    private String client_secret;
    @Value("${googleapi.grantType}")
    private String grant_type;

}

package com.littlejenny.gulimall.paypal.credential;

import com.paypal.base.rest.APIContext;

public class Credential {
    public static String clientId = "Ac3UabNiB0ctYzFrNq4LlKOMrTGITss94DTo0FcfqA38MZ4buJJ8UK3S9L7NRPsQGwEksZ6OscV0Jv1V";
    public static  String clientSecret = "EEy7qbObCKnI1H4UJl2AropC6k2iIMexCEY-Vl_jHLzqIJufPwsL5vwS_Zgy0C-kOIt6vhypj0TmZMlA";

    public static APIContext context = new APIContext(clientId, clientSecret, "sandbox");

}

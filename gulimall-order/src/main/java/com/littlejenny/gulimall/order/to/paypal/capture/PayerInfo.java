/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.capture;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/* Time: 2021-08-28 20:21:32 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
@Data
public class PayerInfo {

    private String email;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("payer_id")
    private String payerId;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("shipping_address")
    private ShippingAddress shippingAddress;


}
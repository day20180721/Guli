/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.create;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/* Time: 2021-08-28 20:19:7 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
@Data
public class Payer {

    @JsonProperty("payment_method")
    private String paymentMethod;

}
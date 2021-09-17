/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.capture;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Time: 2021-08-28 20:21:32 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
public class Details {

    private String subtotal;
    private String shipping;
    @JsonProperty("handling_fee")
    private String handlingFee;
    @JsonProperty("shipping_discount")
    private String shippingDiscount;
    private String insurance;

}
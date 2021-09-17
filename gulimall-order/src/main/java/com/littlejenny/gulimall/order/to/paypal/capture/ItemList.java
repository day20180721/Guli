/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.capture;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/* Time: 2021-08-28 20:21:32 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
@Data
public class ItemList {

    @JsonProperty("shipping_address")
    private ShippingAddress shippingAddress;

}
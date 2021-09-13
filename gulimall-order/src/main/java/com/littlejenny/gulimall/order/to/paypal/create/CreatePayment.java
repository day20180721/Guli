/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.create;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Date;
@Data
/* Time: 2021-08-28 20:19:7 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
public class CreatePayment {

    private String id;
    private String intent;
    private Payer payer;
    private List<Transactions> transactions;
    private String state;
    @JsonProperty("create_time")
    private Date createTime;
    private List<Links> links;


}
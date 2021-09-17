/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.capture;
import java.util.List;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/* Time: 2021-08-28 20:21:32 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
@Data
public class CapturePayment {

    private String id;
    private String intent;
    private Payer payer;
    private String cart;
    private List<Transactions> transactions;
    @JsonProperty("failed_transactions")
    private List<String> failedTransactions;
    private String state;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("update_time")
    private Date updateTime;
    private List<Links> links;


}
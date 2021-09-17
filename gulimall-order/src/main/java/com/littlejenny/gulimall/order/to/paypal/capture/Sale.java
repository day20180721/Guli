/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.capture;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
/* Time: 2021-08-28 20:21:32 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
public class Sale {

    private String id;
    private Amount amount;
    @JsonProperty("payment_mode")
    private String paymentMode;
    private String state;
    @JsonProperty("protection_eligibility")
    private String protectionEligibility;
    @JsonProperty("protection_eligibility_type")
    private String protectionEligibilityType;
    @JsonProperty("transaction_fee")
    private TransactionFee transactionFee;
    @JsonProperty("parent_payment")
    private String parentPayment;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("update_time")
    private Date updateTime;
    private List<Links> links;
    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }



}
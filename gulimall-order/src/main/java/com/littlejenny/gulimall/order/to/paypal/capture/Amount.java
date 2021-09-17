/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.capture;

import com.paypal.api.payments.Details;

/* Time: 2021-08-28 20:21:32 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
public class Amount {

    private String currency;
    private String total;
    private Details details;
    public void setCurrency(String currency) {
         this.currency = currency;
     }
     public String getCurrency() {
         return currency;
     }

    public void setTotal(String total) {
         this.total = total;
     }
     public String getTotal() {
         return total;
     }

    public void setDetails(Details details) {
         this.details = details;
     }
     public Details getDetails() {
         return details;
     }

}
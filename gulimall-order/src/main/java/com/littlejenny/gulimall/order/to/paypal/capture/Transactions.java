/* Copyright 2021 freecodeformat.com */
package com.littlejenny.gulimall.order.to.paypal.capture;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
/* Time: 2021-08-28 20:21:32 @author freecodeformat.com @website http://www.freecodeformat.com/json2javabean.php */
@Data
public class Transactions {

    @JsonProperty("related_resources")
    private List<RelatedResources> relatedResources;
    private Amount amount;
    private Payee payee;
    private String description;
    @JsonProperty("item_list")
    private ItemList itemList;

}
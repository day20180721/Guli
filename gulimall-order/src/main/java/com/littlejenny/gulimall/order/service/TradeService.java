package com.littlejenny.gulimall.order.service;

import com.littlejenny.common.exception.OrderTokenInvalidException;
import com.littlejenny.gulimall.order.vo.OrderConfirmVO;
import com.littlejenny.gulimall.order.vo.SubmitOrderRespVO;
import com.littlejenny.gulimall.order.vo.SubmitOrderVO;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

public interface TradeService {
    OrderConfirmVO trade() throws ExecutionException, InterruptedException;

    SubmitOrderRespVO submitOrder(SubmitOrderVO vo)throws RuntimeException;

    String payByPaypal(String sn);

    void processPaypal(String paymentId, String payerID);
}

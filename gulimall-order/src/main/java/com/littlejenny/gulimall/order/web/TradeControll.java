package com.littlejenny.gulimall.order.web;

import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.exception.OrderTokenInvalidException;
import com.littlejenny.common.exception.RemoteServiceException;
import com.littlejenny.gulimall.order.entity.OrderEntity;
import com.littlejenny.gulimall.order.service.OrderService;
import com.littlejenny.gulimall.order.service.TradeService;
import com.littlejenny.gulimall.order.vo.OrderConfirmVO;
import com.littlejenny.gulimall.order.vo.SubmitOrderRespVO;
import com.littlejenny.gulimall.order.vo.SubmitOrderVO;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class TradeControll {
    @Autowired
    private TradeService tradeService;
    @GetMapping("/trade")
    public String trade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVO orderConfirmVO = tradeService.trade();

        model.addAttribute("detail",orderConfirmVO);
        return "detail";
    }
    @PostMapping("/submitOrder")
    public String submitOrder(SubmitOrderVO vo, HttpSession session){
        SubmitOrderRespVO resp = new SubmitOrderRespVO();
        try {
            resp =  tradeService.submitOrder(vo);
        }catch (OrderTokenInvalidException e){
//            redirectAttributes.addFlashAttribute("msg", "避免重複提交，請操作放慢");
            resp.setCode(BizCodeEnum.ORDERTOKEN_INVALID_EXCEPTION.getCode());
            e.printStackTrace();
            return "redirect:http://order.gulimall.com/trade";
        }catch(RemoteServiceException e){
//            redirectAttributes.addFlashAttribute("msg", "訂單項已售罄，請再次確認訂單");
            resp.setCode(BizCodeEnum.REMOTESERVICE_EXCEPTION.getCode());
            e.printStackTrace();
            return "redirect:http://order.gulimall.com/trade";
        }catch (RuntimeException e){
            resp.setCode(BizCodeEnum.UNKNOW_EXCEPTION.getCode());
            e.printStackTrace();
            return "redirect:http://order.gulimall.com/trade";
        }
        session.setAttribute("resp",resp);
        return "redirect:http://order.gulimall.com/pay.html";
    }

    @GetMapping("/paypal/{orderSn}")
    public String payByPaypal(@PathVariable("orderSn")String sn){
        String approveUrl =  tradeService.payByPaypal(sn);
        if("".equals(approveUrl)){
            log.error("payByPaypal 方法失效");
        }
        return "redirect:" + approveUrl;
    }
    @ResponseBody
    @GetMapping("/paypal/cancle")
    public String cancle(HttpServletRequest request) {
        System.out.println(request.getParameterMap());
        return "cancle";
    }
    @GetMapping("/paypal/process")
    public String proccess(HttpServletRequest request,@RequestParam("paymentId")String paymentId,@RequestParam("PayerID")String PayerID) {
        tradeService.processPaypal(paymentId,PayerID);
        return "redirect:http://order.gulimall.com/orders.html";
    }
}

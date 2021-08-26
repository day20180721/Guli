package com.littlejenny.gulimall.order.web;

import com.littlejenny.common.exception.BizCodeEnum;
import com.littlejenny.common.exception.OrderTokenInvalidException;
import com.littlejenny.common.exception.RemoteServiceException;
import com.littlejenny.gulimall.order.entity.OrderEntity;
import com.littlejenny.gulimall.order.service.TradeService;
import com.littlejenny.gulimall.order.service.impl.OrderServiceImpl;
import com.littlejenny.gulimall.order.vo.OrderConfirmVO;
import com.littlejenny.gulimall.order.vo.SubmitOrderRespVO;
import com.littlejenny.gulimall.order.vo.SubmitOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private OrderServiceImpl orderService;
    /*
    @GetMapping("/t")
    public void t(){
        c();
    }
    @Transactional
    public void c(){
        OrderEntity e = new OrderEntity();
        e.setMemberId(1L);
        orderService.save(e);
        a();
        b();


    }
    @Transactional
    public void a(){
        OrderEntity e = new OrderEntity();
        e.setMemberId(2L);
        orderService.save(e);
        int i = 10 / 0;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void b(){
        OrderEntity e = new OrderEntity();
        e.setMemberId(3L);
        orderService.save(e);
        int i = 10 / 0;
    }

     */
}

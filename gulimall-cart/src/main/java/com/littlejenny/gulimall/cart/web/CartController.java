package com.littlejenny.gulimall.cart.web;

import com.littlejenny.common.utils.R;
import com.littlejenny.gulimall.cart.service.impl.CartServiceImpl;
import com.littlejenny.gulimall.cart.vo.CartVO;
import com.littlejenny.gulimall.cart.vo.CartItemVO;
import com.littlejenny.gulimall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {
    @Autowired
    private CartServiceImpl cartService;
    /**
     * 取得整體購物車資訊
     * @return
     */

    @ResponseBody
    @GetMapping("/cartItems/{mID}")
    public R cartItems(@PathVariable("mID")Long mId){
        List<CartItemVO> items = cartService.getItemsByMID(mId.toString());
        return R.ok().setData(items);
    }
    @ResponseBody
    @GetMapping("/cartItems/new")
    public R cartItemsNewest(){
        List<CartItemVO> items = cartService.getItemsByMIDNewestPrice();
        return R.ok().setData(items);
    }

    @GetMapping("/cart.html")
    public String cartHtml(Model model){
        CartVO cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cart";
    }
    @GetMapping("/updateItem")
    public String updateItemCount(@RequestParam("skuId")Long skuId,@RequestParam("count")Integer count){
        System.out.println(count);
        cartService.updateCartItemCount(skuId,count);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/removeItem")
    public String removeItem(@RequestParam("skuId")Long skuId){
        cartService.removeCartItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
    /**
     * 放東西進入購物車，記得最後要轉發，否則會有重複提交的問題
     * @return
     */
    @GetMapping("/cartItem/check")
    public String cartItemCkech(@RequestParam("skuId")Long skuId,@RequestParam("check")Integer check){
        cartService.updateCartItemCheck(skuId,check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/cart")
    public String cart(@RequestParam("skuId")Long skuId, @RequestParam("count")Integer count, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.itemTOcart(skuId,count);
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/success.html";
    }
    @GetMapping("/success.html")
    public String successHtml(@RequestParam("skuId")Long skuId, Model model){
        CartItemVO item = cartService.getCartItemBySkuId(skuId);
        model.addAttribute("item",item);
        return "success";
    }
}

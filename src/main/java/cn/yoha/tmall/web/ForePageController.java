package cn.yoha.tmall.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForePageController {
    @GetMapping("/")
    public String index(){
        return "redirect:home";
    }
    @GetMapping("/home")
    public String home(){
        return "fore/home";
    }
    @GetMapping("/register")
    public String register(){
        return "fore/register";
    }
    @GetMapping("/registerSuccess")
    public String registerSucc(){
        return "fore/registerSuccess";
    }
    @GetMapping("/login")
    public String login(){
        return "fore/login";
    }
    @GetMapping("/forelogout")
    public String logout( ){
        //旧的logout方式为删除session中的user数据
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated())
            subject.logout();
        return "redirect:home";
    }
    @GetMapping("/product")
    public String showProduct(){
        return "fore/product";
    }
    @GetMapping("/category")
    public String showCategory(){
        return "fore/category";
    }
    @GetMapping("/search")
    public String searchPage(){
        return "fore/search";
    }
    @GetMapping("/buy")
    public String buyPage(){
        return "fore/buy";
    }
    @GetMapping("/cart")
    public String cartPage(){
        return "fore/cart";
    }
    @GetMapping("/alipay")
    public String toPay(){
        return "fore/alipay";
    }
    @GetMapping("/payed")
    public String payed(){
        return "fore/payed";
    }
    @GetMapping("/bought")
    public String showOrder(){
        return "fore/bought";
    }
    @GetMapping("/confirmPay")
    public String confirmPay(){
        return "fore/confirmPay";
    }
    @GetMapping("/orderConfirmed")
    public String orderConfirmed(){
        return "fore/orderConfirmed";
    }
    @GetMapping("/review")
    public String reviewPage(){
        return "fore/review";
    }
}

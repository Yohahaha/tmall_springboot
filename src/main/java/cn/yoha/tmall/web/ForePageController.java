package cn.yoha.tmall.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

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
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "fore/home";
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
}

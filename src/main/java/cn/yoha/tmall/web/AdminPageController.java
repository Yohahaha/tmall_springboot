package cn.yoha.tmall.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {
    //分类管理相关页面
    @GetMapping("/admin")
    public String admin() {
        return "redirect:admin_category_list";
    }

    @GetMapping("/admin_category_list")
    public String listCategory() {
        return "admin/listCategory";
    }

    @GetMapping("/admin_category_edit")
    public String editCategory() {
        return "admin/editCategory";
    }
    //属性相关页面
    @GetMapping("/admin_property_list")
    public String listProperty() {
        return "admin/listProperty";
    }

    @GetMapping("/admin_property_edit")
    public String editProperty() {
        return "admin/editProperty";
    }
    //产品相关页面
    @GetMapping("/admin_product_list")
    public String listProduct() {
        return "admin/listProduct";
    }

    @GetMapping("/admin_product_edit")
    public String editProduct() {
        return "admin/editProduct";
    }
    //产品图片页面
    @GetMapping("/admin_productImage_list")
    public String listProductImage() {
        return "admin/listProductImage";
    }
    //用户管理页面
    @GetMapping("/admin_user_list")
    public String listUser(){
        return "admin/listUser";
    }
    @GetMapping(value="/admin_order_list")
    public String listOrder(){
        return "admin/listOrder";

    }
}

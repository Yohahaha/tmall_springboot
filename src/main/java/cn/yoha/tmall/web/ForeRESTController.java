package cn.yoha.tmall.web;

import cn.yoha.tmall.comparator.*;
import cn.yoha.tmall.pojo.*;
import cn.yoha.tmall.service.*;
import cn.yoha.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ForeRESTController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private PropertyValueService propertyValueService;
    @Autowired
    private ProductImageService productImageService;
    @GetMapping("/forehome")
    public Object home() {
        List<Category> categoryList = categoryService.list();
        productService.fill(categoryList);
        productService.fillByRow(categoryList);
        categoryService.removeCategoryFromProduct(categoryList);
        return categoryList;
    }

    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        boolean exist = userService.isExist(name);
        if (exist) {
            return Result.fail("该用户已经被注册。");
        }
        userService.add(user);
        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);
        User user = userService.loginByNameAndPassword(name, userParam.getPassword());
        if (null != user) {
            session.setAttribute("user", user);
            return Result.success();
        }
        return Result.fail("用户名或密码错误。");
    }

    @GetMapping("/foreproduct/{pid}")
    public Object showProduct(@PathVariable Integer pid) {
        Product product = (Product) productService.get(pid);
        //查询该产品的所有评论
        List<Review> reviewList = reviewService.listAllReview(product);
        //获取该产品的销量
        productService.setSaleAndReview(product);
        //获取该产品的所有属性值以及各种照片
        List<PropertyValue> propertyValueList = propertyValueService.list(product);
        for (PropertyValue value:propertyValueList){
            value.setProduct(null);
            value.getProperty().setCategory(null);
        }
        List<ProductImage> singleProductImages = productImageService.listSingleProductImages(product);
        List<ProductImage> detailProductImages = productImageService.listDetailProductImages(product);
        productImageService.setFirstProdutImage(product);
        product.setProductSingleImages(singleProductImages);
        product.setProductDetailImages(detailProductImages);
        Map<String,Object> model = new HashMap<>();
        model.put("product",product);
        model.put("pvs",propertyValueList);
        model.put("reviews",reviewList);
        return Result.success(model);
    }

    @GetMapping("/forecheckLogin")
    public Object checkLogin(HttpSession session){
        User user =(User) session.getAttribute("user");
        if (null!=user){
            return Result.success();
        }
        return Result.fail("");
    }

    @GetMapping("/forecategory/{cid}")
    public Object category(@PathVariable int cid,@RequestParam String sort){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReview(category.getProducts());
        categoryService.removeCategoryFromProduct(category);

        if (null!=sort){
            switch (sort){
                case "review":
                    category.getProducts().sort(new ProductReviewComparator());
                    break;
                case "date":
                    category.getProducts().sort(new ProductDateComparator());
                    break;
                case "saleCount":
                    category.getProducts().sort(new ProductSaleCountComparator());
                    break;
                case "price":
                    category.getProducts().sort(new ProductPriceComparator());
                    break;
                case "all":
                    category.getProducts().sort(new ProductAllComparator());
                    break;
            }
        }
        return category;
    }

    @PostMapping("/foresearch")
    public Object search(@RequestParam String keyword){
        if (null==keyword)
            keyword = "";
        List<Product> list = productService.search(keyword, 0, 20);
        productImageService.setFirstProdutImages(list);
        productService.setSaleAndReview(list);
        return list;
    }
}

package cn.yoha.tmall.web;

import cn.yoha.tmall.comparator.*;
import cn.yoha.tmall.pojo.*;
import cn.yoha.tmall.service.*;
import cn.yoha.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private OrderService orderService;

    /**
     * 展示首页
     */
    @GetMapping("/forehome")
    public Object home() {
        List<Category> categoryList = categoryService.list();
        //填充产品数据
        productService.fill(categoryList);
        //填充产品推荐数据
        productService.fillByRow(categoryList);
        //移除分类中的产品的分类属性，防止无限递归
        categoryService.removeCategoryFromProduct(categoryList);
        return categoryList;
    }

    /**
     * 注册方法
     */
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

    /**
     * 登录方法
     *
     * @return
     */
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);
        User user = userService.loginByNameAndPassword(name, userParam.getPassword());
        if (null != user) {
            //登录成功，将user数据存入session
            user.setPassword(null);
            session.setAttribute("user", user);
            return Result.success();
        }
        return Result.fail("用户名或密码错误。");
    }

    /**
     * 产品详情
     */
    @GetMapping("/foreproduct/{pid}")
    public Object showProduct(@PathVariable Integer pid) {
        Product product = (Product) productService.get(pid);
        //查询该产品的所有评论
        List<Review> reviewList = reviewService.listAllReview(product);
        //获取该产品的销量
        productService.setSaleAndReview(product);
        //获取该产品的所有属性值以及各种照片
        List<PropertyValue> propertyValueList = propertyValueService.list(product);
        for (PropertyValue value : propertyValueList) {
            value.setProduct(null);
            value.getProperty().setCategory(null);
        }
        //获取产品的图片
        List<ProductImage> singleProductImages = productImageService.listSingleProductImages(product);
        List<ProductImage> detailProductImages = productImageService.listDetailProductImages(product);
        productImageService.setFirstProdutImage(product);
        product.setProductSingleImages(singleProductImages);
        product.setProductDetailImages(detailProductImages);
        //用map返回
        Map<String, Object> model = new HashMap<>();
        model.put("product", product);
        model.put("pvs", propertyValueList);
        model.put("reviews", reviewList);
        return Result.success(model);
    }

    /**
     * 检查是否登录
     */
    @GetMapping("/forecheckLogin")
    public Object checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null != user) {
            return Result.success();
        }
        return Result.fail("");
    }

    /**
     * 分类查询
     */
    @GetMapping("/forecategory/{cid}")
    public Object category(@PathVariable int cid, @RequestParam String sort) {
        //根据cid获取分类数据，并对其进行数据填充
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReview(category.getProducts());
        categoryService.removeCategoryFromProduct(category);
        //根据传入参数对其进行排序
        if (null != sort) {
            switch (sort) {
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

    /**
     * 搜索方法--通过sql的like查询实现
     */
    @PostMapping("/foresearch")
    public Object search(@RequestParam String keyword) {
        if (null == keyword)
            keyword = "";
        List<Product> list = productService.search(keyword, 0, 20);
        productImageService.setFirstProdutImages(list);
        productService.setSaleAndReview(list);
        return list;
    }

    /**
     * 点击【立即购买】按钮后向数据库中添加订单项，随后在前端页面中跳转到forebuy路径
     */
    @GetMapping("/forebuyone")
    public int buyone(@RequestParam int pid, @RequestParam int num, HttpSession session) {
        int oiid = buyAndAddCart(pid, num, session);

        return oiid;
    }

    private int buyAndAddCart(int pid, int num, HttpSession session) {
        Product product = (Product) productService.get(pid);
        User user = (User) session.getAttribute("user");
        int oiid = 0;
        boolean found = false;
        //查询该用户对应的所有未成订单的订单项
        List<OrderItem> itemList = orderItemService.listByUser(user);
        //遍历每一个item，如果找到和添加的产品id相同的订单项，则更新其数量并更新数据库
        for (OrderItem item : itemList) {
            if (item.getProduct().getId() == pid) {
                item.setNumber(num + item.getNumber());
                orderItemService.update(item);
                oiid = item.getId();
                found = true;
                break;
            }
        }
        //如果没有找到已经存在的订单项，则这是一个新订单项
        if (!found) {
            OrderItem item = new OrderItem();
            item.setNumber(num);
            item.setProduct(product);
            item.setOrder(null);
            item.setUser(user);
            oiid = orderItemService.update(item).getId();
        }
        return oiid;
    }

    /**
     * 结算界面的统一入口
     * - 如果是从立即购买进入的，只查询当前要购买的订单项信息和总价
     * - 如果是从购物车进入的，则根据其传来的oiids数组确定订单项
     */
    @GetMapping("/forebuy")
    public Object checkBuy(String[] oiid, HttpSession session) {
        List<OrderItem> itemList = new ArrayList<>();
        float total = 0;
        for (String oid : oiid) {
            OrderItem item = orderItemService.get(Integer.valueOf(oid));
            total += item.getNumber() * item.getProduct().getPromotePrice();
            item.getUser().setPassword(null);
            itemList.add(item);
        }
        session.setAttribute("ois", itemList);
        productImageService.setFirstProductImagesOnOrderItems(itemList);
        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", itemList);
        map.put("total", total);
        return Result.success(map);
    }

    /**
     * 点击【加入购物车】按钮后请求发送到这里，处理订单项信息
     */
    @GetMapping("foreaddCart")
    public Object addCart(@RequestParam int pid, @RequestParam int num, HttpSession session) {
        int oiid = buyAndAddCart(pid, num, session);
        return Result.success();
    }

    /**
     * 展示购物车
     */
    @GetMapping("/forecart")
    public Object showCart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> itemList = orderItemService.listByUser(user);
        productImageService.setFirstProductImagesOnOrderItems(itemList);
        return itemList;
    }

    /**
     * 购物车页面中调整订单项数目的方法
     */
    @GetMapping("/forechangeOrderItem")
    public Object changeItem(@RequestParam int pid, @RequestParam int num, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("用户未登录");
        }
        //查询出购物车中的所有商品，对其进行更新数量
        List<OrderItem> itemList = orderItemService.listByUser(user);
        for (OrderItem item : itemList) {
            if (item.getProduct().getId() == pid) {
                item.setNumber(num);
                orderItemService.update(item);
                break;
            }
        }
        return Result.success();
    }

    /**
     * 购物车页面中删除订单项
     */
    @GetMapping("/foredeleteOrderItem")
    public Object deleteItem(@RequestParam int oiid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("用户未登录");
        }
        List<OrderItem> itemList = orderItemService.listByUser(user);
        for (OrderItem item : itemList) {
            if (item.getId() == oiid) {
                orderItemService.delete(oiid);
                break;
            }
        }
        return Result.success();
    }

    /**
     * 创建订单
     */
    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("用户未登录");
        //填充订单相关数据，传入的订单已经包含地址联系人等信息
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        //从session中获取上一步购物车中确定要购买的订单项
        List<OrderItem> itemList = (List<OrderItem>) session.getAttribute("ois");
        //计算总价
        float total = 0;
        for (OrderItem item : itemList) {
            total += item.getProduct().getPromotePrice() * item.getNumber();
            item.setOrder(order);
            item.getUser().setPassword(null);
        }
        order.setTotal(total);
        order.getUser().setPassword(null);
        Order reOrder = orderService.add(order);
        Map<String, Object> map = new HashMap<>();
        map.put("oid", reOrder.getId());
        map.put("total", total);
        return Result.success(map);
    }

    /**
     * 点击确定支付后跳转payed页面，映射到该方法
     */
    @GetMapping("/forepayed")
    public Object payed(@RequestParam int oid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user) {
            return Result.fail("用户未登录");
        }
        //获取订单
        Order order = orderService.get(oid);
        //设置为待发货状态
        order.setStatus(OrderService.waitDelivery);
        //设置支付时间并更新到数据库
        order.setPayDate(new Date());
        orderService.update(order);
        //计算预估到货时间
        Date payDate = order.getPayDate();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(payDate);
        calendar.add(Calendar.DATE, 4);
        Date time = calendar.getTime();
        String month = new SimpleDateFormat("MM").format(time);
        String day = new SimpleDateFormat("dd").format(time);
        System.out.println("---------" + month + "---" + day + "--------------");
        Map<String, Object> map = new HashMap<>();
        map.put("order", order);
        map.put("m", month);
        map.put("d", day);
        return Result.success(map);
    }

    /**
     * 查看所有订单
     */
    @GetMapping("/forebought")
    public Object showOrders(HttpSession session) {
        User user = (User) session.getAttribute("user");
        //展示该用户的所有未标记delete状态订单
        List<Order> orders = orderService.listByUserWithoutDelete(user);
        //防止json序列化时爆栈
        orderService.removeOrderFromOrderItem(orders);
        return orders;
    }

    /**
     * 删除订单
     */
    @PutMapping("/foredeleteOrder")
    public Object deleteOrder(@RequestParam int oid,HttpSession session){
        User user =(User) session.getAttribute("user");
        if (null==user){
            return Result.fail("用户未登录");
        }
        orderService.deleteOrder(oid);
        return Result.success();
    }

    /**
     * 点击【确认收货】按钮后进入确认收货页面，此方法在加载页面时被调用用来展示订单信息
     */
    @GetMapping("/foreconfirmPay")
    public Object confirmPay(@RequestParam int oid){
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        return order;
    }

    /**
     * 确认收货
     */
    @GetMapping("/foreorderConfirmed")
    public Object orderConfirmed(@RequestParam int oid){
        //获取订单，修改其状态为等待评价，修改其确认支付时间，更新到数据库
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setPayDate(new Date());
        orderService.update(order);
        return Result.success();
    }

    /**
     * 订单页面点击【评价】按钮跳转到review页面，加载此方法展示订单、商品、已有的评价数据
     * 目前只实现对订单中的第一个订单项进行评价
     */
    @GetMapping("/forereview")
    public Object reviewPage(@RequestParam int oid){
        //获取订单，填充订单项数据
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        //获取第一个产品
        Product product = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.listAllReview(product);
        productService.setSaleAndReview(product);
        Map<String,Object> map = new HashMap<>();
        map.put("p",product);
        map.put("o",order);
        map.put("reviews",reviews);
        return Result.success(map);
    }

    /**
     * 对商品进行评价
     */
    @PostMapping("/foredoreview")
    public Object review(@RequestParam int oid,@RequestParam int pid,@RequestParam String content,HttpSession session){
        //获取订单，修改其状态为finish，更新到数据库
        Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);
        //获取该产品，获取评价内容content，对content进行转义，从session中获取user，然后new一个review对象，填充数据后更新到数据库中
        Product product =(Product) productService.get(pid);
        content = HtmlUtils.htmlEscape(content);
        User user =(User) session.getAttribute("user");
        Review review = new Review();
        review.setProduct(product);
        review.setCreateDate(new Date());
        review.setUser(user);
        review.setContent(content);
        reviewService.addReview(review);
        return Result.success();
    }
}

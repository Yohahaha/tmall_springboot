package cn.yoha.tmall.interceptor;

import cn.yoha.tmall.pojo.Category;
import cn.yoha.tmall.pojo.OrderItem;
import cn.yoha.tmall.pojo.User;
import cn.yoha.tmall.service.CategoryService;
import cn.yoha.tmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 处理全局的购物车数量，点击图片回到首页，搜索栏下的分类推荐
 */
public class OtherInterceptor implements HandlerInterceptor {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderItemService orderItemService;
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HttpSession session = request.getSession();
        User user =(User) session.getAttribute("user");
        Integer cartTotalItemNumber = 0;
        if (user!=null){
            //要对session中的user进行判断，否则会在下面的方法中出错
            List<OrderItem> itemList = orderItemService.listByUser(user);
            for (OrderItem item: itemList){
                cartTotalItemNumber += item.getNumber();
            }
        }
        session.setAttribute("cartTotalItemNumber",cartTotalItemNumber);

        List<Category> list = categoryService.list();
        request.getServletContext().setAttribute("categories_below_search",list);
        request.getServletContext().setAttribute("contextPath","/");
    }
}

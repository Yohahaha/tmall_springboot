package cn.yoha.tmall.interceptor;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String[] requireAuthPages = new String[]{
                "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",

                "forebuyone",
                "forebuy",
                "foreaddCart",
                "forecart",
                "forechangeOrderItem",
                "foredeleteOrderItem",
                "forecreateOrder",
                "forepayed",
                "forebought",
                "foreconfirmPay",
                "foreorderConfirmed",
                "foredeleteOrder",
                "forereview",
                "foredoreview"
        };
        //对请求uri进行处理
        String uri = request.getRequestURI();
        uri = uri.substring(1);
        //判断其是否是需要拦截的uri
        if (beginWith(uri,requireAuthPages)){
//            --------旧的验证是否登录的方式------
//            User user =(User) session.getAttribute("user");
//            if (null==user){
//                response.sendRedirect("login");
//                return false;
//            }
            Subject subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()){
                response.sendRedirect("login");
                return false;
            }
        }
        return true;
    }

    private boolean beginWith(String uri, String[] requireAuthPages) {
        boolean flag = false;
        for (String path:requireAuthPages){
            if (uri.startsWith(path)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}

package cn.yoha.tmall.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception{
        e.printStackTrace();
        Class constrainViolationException = Class.forName("org.hibernate.exception.ConstraintViolationException");
        if (null != e.getCause() && constrainViolationException == e.getCause().getClass()){
            return "违反了约束，多半是外键约束";
        }
        return e.getMessage();
    }
}

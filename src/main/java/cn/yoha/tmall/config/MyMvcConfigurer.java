package cn.yoha.tmall.config;

import cn.yoha.tmall.interceptor.LoginInterceptor;
import cn.yoha.tmall.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getloginInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(getOtherInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public LoginInterceptor getloginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public OtherInterceptor getOtherInterceptor() {
        return new OtherInterceptor();
    }
}

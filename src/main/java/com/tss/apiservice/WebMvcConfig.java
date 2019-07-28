package com.tss.apiservice;

import com.tss.apiservice.Interceptor.PermissionInterceptor;
import com.tss.apiservice.Interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    RequestInterceptor requestInterceptor;//所有请求都需要的拦截器

    @Autowired
    PermissionInterceptor permissionInterceptor;

    /**
     * 添加静态资源文件，外部可以直接访问地址
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

    }

    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(requestInterceptor).addPathPatterns("/**"); //暂时解决nginx跨域问题，可以把前端代码全部去掉
//        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
    }
}
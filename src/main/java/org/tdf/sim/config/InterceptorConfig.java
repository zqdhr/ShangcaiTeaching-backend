package org.tdf.sim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig  implements WebMvcConfigurer {

//    //放开所有静态资源拦截
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截
        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/*");

    }
}

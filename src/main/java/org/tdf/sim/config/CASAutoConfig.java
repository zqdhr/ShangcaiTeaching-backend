package org.tdf.sim.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CASAutoConfig {

    @Value("${cas.server-login-url}")
    private String serverLoginUrl;

    @Value("${cas.client-host-url}")
    private String clientHostUrl;

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> filterAuthenticationRegistraction(){
        System.out.println("=============="+serverLoginUrl);
        FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<AuthenticationFilter>();
        registration.setFilter(new AuthenticationFilter());

        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        Map<String,String> initParameters = new HashMap<String,String>();
        initParameters.put("casServerLoginUrl",serverLoginUrl);
        initParameters.put("serverName",clientHostUrl);
        // 忽略的url，"|"分隔多个url
        initParameters.put("ignorePattern", "/cas/logout|/cas/login|/error");
        registration.setInitParameters(initParameters);
        registration.setOrder(1);
        return registration;
    }
}

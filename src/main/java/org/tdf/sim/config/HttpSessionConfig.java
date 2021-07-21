package org.tdf.sim.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpSessionListener;

@Configuration
public class HttpSessionConfig implements WebMvcConfigurer {

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> listenerRegister() {
        ServletListenerRegistrationBean<HttpSessionListener> registrationBean = new ServletListenerRegistrationBean<>();
        registrationBean.setListener(new TDSHttpSessionListener());
        return registrationBean;
    }

}

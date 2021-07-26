package org.tdf.sim.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.tdf.sim.util.ConstantUtils;
import org.tdf.sim.util.LoginUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("url:"+ request.getRequestURI());
        LoginUser userEntity = (LoginUser) request.getSession().getAttribute(ConstantUtils.USER_SESSION_KEY);
        System.out.println("userEntity:"+ userEntity);
        if(userEntity == null){
            response.sendRedirect(request.getContextPath() + "/cas/login");
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}

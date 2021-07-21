package org.tdf.sim.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tdf.sim.type.Response;

// 全局 rpc 异常处理
@ControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler
    public Object notFoundException(final Exception e) {
        e.printStackTrace();
        if (e.getMessage().equals("jwt is invalid")) {
            return Response.error(Response.Code.JWT_INVALID);
        }
        return Response.error(Response.Code.INTERNAL_ERROR, e.getMessage());
    }
}

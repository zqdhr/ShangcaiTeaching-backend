package org.tdf.sim.controller;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.tdf.sim.type.Response;

// 全局 rpc 返回处理
@RestControllerAdvice
public class CommonAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body instanceof byte[] ||
                body instanceof String ||
                body instanceof Response) {
            return body;
        }
        try {
            return Response.success(body);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(Response.Code.INTERNAL_ERROR);
        }
    }
}

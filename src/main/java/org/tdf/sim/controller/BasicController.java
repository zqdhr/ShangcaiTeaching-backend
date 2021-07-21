package org.tdf.sim.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tdf.sim.Constants;
import org.tdf.sim.service.SecurityService;
import org.tdf.sim.type.Response;
import org.tdf.sim.type.SignInPost;
import org.tdf.sim.type.SignInResp;

import static org.tdf.sim.Constants.JWT_HEADER_KEY;

@RestController
@RequestMapping(Constants.API_V1_PREFIX)
@RequiredArgsConstructor
public class BasicController {
    private final SecurityService securityService;

    @GetMapping("/echo")
    public String echo(@RequestParam(value = "msg", required = false) String msg) {
        return msg == null ? "" : msg;
    }

    // 登录，返回登录凭证 jwt
    @PostMapping(value = "/session", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SignInResp signIn(@RequestBody @Validated SignInPost signInPost) {
        String jwt = securityService.createJWT(signInPost.getId());
        return new SignInResp(signInPost.getId(), jwt);
    }

    // 查看当前登录用户，需要鉴权，Http 头部需要 Authorization 字段
    @GetMapping("/session")
    public Response<String> getSession(@RequestHeader(JWT_HEADER_KEY) String jwt) {
        String data = securityService.getUserId(jwt);
        return Response.success(data);
    }
}

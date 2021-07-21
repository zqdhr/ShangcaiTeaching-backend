package org.tdf.sim.type;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * rest api 全局返回格式
 * <p>
 * {
 * "code": 500,
 * "data": "data",
 * "message": "success"
 * }
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Response<T> {
    @JsonProperty("code")
    private int code;

    @JsonProperty("data")
    private T data;

    @JsonProperty("message")
    private String message;

    public static <T> Response<T> success(T data) {
        return new Response<>(Code.SUCCESS.code, data, Code.SUCCESS.message);
    }

    public static <T> Response<T> success() {
        return new Response<>(Code.SUCCESS.code, null, Code.SUCCESS.message);
    }

    public static <T> Response<T> success(Code code, T data, String message) {
        return new Response<>(code.code, data, message + code.message);
    }

    public static <T> Response<T> error(Code code) {
        return new Response<>(code.code, null, code.message);
    }


    public static <T> Response<T> error(Code code, String reason) {
        return new Response<>(code.code, null, reason);
    }

    public static <T> Response<T> error(String reason) {
        return new Response<>(Code.INTERNAL_ERROR.code, null, reason);
    }

    public static <T> Response<T> error(Code code, String reason,T data) {
        return new Response<>(code.code, data, reason);
    }

    public enum Code {
        SUCCESS(200, "success"),

        SUCCESS_WEBSOCKET_LOGIN(201, "加入成功"),

        SUCCESS_WEBSOCKET_LOGOUT(202, "退出成功"),

        SUCCESS_WEBSOCKET_ISSUECERRENCY(203, "代币发行成功"),

        SUCCESS_WEBSOCKET_TRANSFER(204, "转账成功"),

        SUCCESS_WEBSOCKET_ROBOT(205, "添加机器人成功"),

        INTERNAL_ERROR(500, "internal error"),

        LOGIN_REPEAT_ERROR(501, "已添加"),

        JWT_INVALID(400, "jwt is valid"),

        JWT_EXPIRED(401, "登录超时");

        public final int code;

        public final String message;

        Code(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}


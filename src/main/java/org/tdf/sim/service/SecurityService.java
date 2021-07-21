package org.tdf.sim.service;

/**
 * 登录、权限管理
 */
public interface SecurityService {

    /**
     * 从 json web token (jwt) 读取用户 id
     *
     * @param jwt jwt
     * @return 用户 id
     */
    String getUserId(String jwt);

    /**
     * 根据 用户 id 构造 jwt
     *
     * @param userId 用户数据库主键
     * @return 生成好的 jwt
     */
    String createJWT(String userId);
}

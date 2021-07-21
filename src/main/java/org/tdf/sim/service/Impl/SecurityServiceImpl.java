package org.tdf.sim.service.Impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tdf.sim.config.JWTConfig;
import org.tdf.sim.dao.UserDao;
import org.tdf.sim.service.SecurityService;

import javax.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类，可以生成登录凭证
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "security")
public class SecurityServiceImpl implements SecurityService {
    private static final String USER_ID = "user-id";
    private static final String USER_SUBJECT = "sub";

    private final JWTConfig jwtConfig;
    private final UserDao userDao;

    private SignatureAlgorithm signatureAlgorithm;

    private PrivateKey privateKey;


    @PostConstruct
    public void init() throws Exception {
        this.signatureAlgorithm =
                Arrays.stream(SignatureAlgorithm.values())
                        .filter(x -> x.getValue().equals(jwtConfig.getAlgorithm().toUpperCase()))
                        .findAny()
                        .orElseThrow(IllegalArgumentException::new);

        if (jwtConfig.getPrivateKey() != null && !jwtConfig.getPrivateKey().isEmpty()) {
            String content =
                    jwtConfig.getPrivateKey();

            content =
                    content
                            .replaceAll("\\n", "")
                            .replace("-----BEGIN PRIVATE KEY-----", "")
                            .replace("-----END PRIVATE KEY-----", "");

            PKCS8EncodedKeySpec spec =
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(content));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.privateKey = kf.generatePrivate(spec);
            return;
        }
        this.privateKey = Keys.keyPairFor(signatureAlgorithm).getPrivate();
        log.info("generate rss private key = {}", Base64.getEncoder().encodeToString(this.privateKey.getEncoded()));
    }

    /**
     * 从 jwt 读取用户 id
     *
     * @param jwt jwt
     * @return
     */
    public String getUserId(@NonNull String jwt) {
        Claims body;
        try{
             body = parseJWT(jwt)
                    .getBody();
        }catch (Exception e){
            throw new RuntimeException("jwt is invalid");
        }

        if (!USER_SUBJECT.equals(body.getSubject())) {
            throw new RuntimeException("无效的 jwt " + body.getSubject());
        }
        return body.get(USER_ID, String.class);
    }

    /**
     * 根据 用户 id 构造 jwt token
     *
     * @param userId 用户数据库主键
     * @return 生成好的 jwt token
     */
    public String createJWT(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, userId);
        return createJWT(claims, USER_SUBJECT);
    }

    @SneakyThrows
    private String createJWT(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setSubject(subject)
                .signWith(privateKey, signatureAlgorithm);

        long expMillis = now.getTime()
                + TimeUnit.SECONDS.toMillis(jwtConfig.getExpiredIn());

        Date exp = new Date(expMillis);
        builder.setExpiration(exp);
        return builder.compact();
    }

    /**
     * 验证 jwt 合法性并解析
     *
     * @param jwt
     */
    private Jws<Claims> parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(privateKey)
                .parseClaimsJws(jwt);
    }
}

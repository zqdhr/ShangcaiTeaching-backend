package org.tdf.sim.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "sim.jwt")
@Component
@Data
public class JWTConfig {
    private long expiredIn;
    private String algorithm;
    private String privateKey;
}

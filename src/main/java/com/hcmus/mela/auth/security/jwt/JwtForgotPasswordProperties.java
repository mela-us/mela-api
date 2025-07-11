package com.hcmus.mela.auth.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "forgot.pw")
public class JwtForgotPasswordProperties {

    private String issuer;

    private String secretKey;

    private long expirationMinute;
}

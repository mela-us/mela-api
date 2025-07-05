package com.hcmus.mela.auth.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hcmus.mela.auth.exception.ForgotPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenForgotPasswordService {
    private final JwtForgotPasswordProperties jwtForgotPasswordProperties;

    public String generateToken(String username) {
        if (jwtForgotPasswordProperties.getSecretKey() == null) {
            throw new ForgotPasswordException("Forgot password secret key is not configured!");
        }
        return JWT.create()
                .withSubject(username)
                .withIssuer(jwtForgotPasswordProperties.getIssuer())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()
                        + jwtForgotPasswordProperties.getExpirationMinute() * 60 * 1000))
                .sign(Algorithm.HMAC256(jwtForgotPasswordProperties.getSecretKey().getBytes()));
    }

    public boolean validateToken(String token, String username) {
        try {
            if (jwtForgotPasswordProperties.getSecretKey() == null) {
                throw new ForgotPasswordException("Forgot password secret key is not configured!");
            }
            final JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(jwtForgotPasswordProperties.getSecretKey().getBytes())).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            return (decodedJWT.getExpiresAt().after(new Date()) && decodedJWT.getSubject().equals(username));
        } catch (Exception ex) {
            return false;
        }
    }
}

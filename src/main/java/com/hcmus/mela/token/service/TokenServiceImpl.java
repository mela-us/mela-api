package com.hcmus.mela.token.service;

import com.hcmus.mela.token.dto.response.GetUserTokenResponse;
import com.hcmus.mela.token.dto.response.IncreaseUserTokenResponse;
import com.hcmus.mela.token.model.Token;
import com.hcmus.mela.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final Integer DEFAULT_TOKENS = 5;
    private final TokenRepository tokenRepository;

    @Override
    public GetUserTokenResponse getUserToken(UUID userId) {
        Token token = tokenRepository.findByUserId(userId).orElse(null);
        if (token == null) {
            token = new Token(userId, DEFAULT_TOKENS);
            Token result = tokenRepository.save(token);
        }
        return new GetUserTokenResponse("Get user token successfully", token.getToken());
    }

    @Override
    public IncreaseUserTokenResponse increaseUserToken(UUID userId, int token) {
        Token userToken = tokenRepository.findByUserId(userId).orElse(null);
        if (userToken == null) {
            userToken = new Token(userId, DEFAULT_TOKENS);
        }
        userToken.setToken(userToken.getToken() + token);
        tokenRepository.save(userToken);
        return new IncreaseUserTokenResponse("Increase user token successfully", userToken.getToken());
    }

    @Override
    public Integer reduceUserToken(UUID userId, Integer cost) {
        Token token = tokenRepository.findByUserId(userId).orElse(null);
        if (token == null) {
            token = new Token(userId, DEFAULT_TOKENS);
        }
        token.setToken(token.getToken() - cost);
        tokenRepository.save(token);
        return token.getToken();
    }

    @Override
    public Boolean validateUserToken(UUID userId) {
        Token token = tokenRepository.findByUserId(userId).orElse(null);
        if (token == null) {
            token = new Token(userId, DEFAULT_TOKENS);
            tokenRepository.save(token);
        }
        return token.getToken() > 0;
    }

    @Override
    public void deleteUserToken(UUID userId) {
        tokenRepository.findByUserId(userId).ifPresent(tokenRepository::delete);
    }
}

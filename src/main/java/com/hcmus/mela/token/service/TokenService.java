package com.hcmus.mela.token.service;

import com.hcmus.mela.token.dto.response.GetUserTokenResponse;
import com.hcmus.mela.token.dto.response.IncreaseUserTokenResponse;

import java.util.UUID;

public interface TokenService {

    GetUserTokenResponse getUserToken(UUID userId);

    IncreaseUserTokenResponse increaseUserToken(UUID userId, int token);

    Integer reduceUserToken(UUID userId, Integer cost);

    Boolean validateUserToken(UUID userId);

    void deleteUserToken(UUID userId);
}

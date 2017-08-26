package com.stephen.bangbang.base.authorization;

public interface TokenManager {
    TokenModel createToken(Long userId);
    boolean checkToken(TokenModel tokenModel);
    TokenModel getToken(Long userId);
    TokenModel getToken(String token);
    void deleteToken(Long userId);
}

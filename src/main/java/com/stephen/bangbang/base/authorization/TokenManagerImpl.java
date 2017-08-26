package com.stephen.bangbang.base.authorization;

import com.stephen.bangbang.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component("tokenManager")
public class TokenManagerImpl implements TokenManager {
    private RedisTemplate<Long, String> redisTemplate;

    @Autowired
    public TokenManagerImpl(RedisTemplate<Long, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public TokenModel createToken(Long userId) {
        final String token = userId + "-" + UUID.randomUUID().toString().replace("-", "");
        redisTemplate.boundValueOps(userId).set(token, Constants.TOKEN_EXPIRES_DAYS, TimeUnit.DAYS);
        return new TokenModel(userId, token);
    }

    @Override
    public boolean checkToken(TokenModel tokenModel) {
        if (tokenModel == null ||
                tokenModel.getUserId() == null ||
                tokenModel.getToken() == null) {
            return false;
        }
        String token = redisTemplate.boundValueOps(tokenModel.getUserId()).get();
        if (token == null || !token.equals(tokenModel.getToken())) {
            return false;
        }
        redisTemplate.boundValueOps(tokenModel.getUserId()).expire(Constants.TOKEN_EXPIRES_DAYS, TimeUnit.DAYS);
        return true;
    }

    @Override
    public TokenModel getToken(Long userId) {
        if (userId == null)
            return null;
        String token = redisTemplate.boundValueOps(userId).get();

        if (token == null)
            return null;

        return new TokenModel(userId, token);
    }

    @Override
    public TokenModel getToken(String token) {
        // 统一检查token不正确性
        try {
            String[] parts = token.split("-");
            Long userId = Long.parseLong(parts[0]);
            return new TokenModel(userId, token);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteToken(Long userId) {
        redisTemplate.delete(userId);
    }
}

package com.stephen.bangbang.authorization;

import com.stephen.bangbang.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

// 并发访问如何配置？？
@Component
public class TokenManagerImpl implements TokenManager {
    private RedisTemplate<Long, String> redisTemplate;

    @Autowired
    public TokenManagerImpl(RedisTemplate<Long, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public TokenModel createToken(Long userId) {
        String token = userId + "-" + UUID.randomUUID().toString().replace("-", "");
        TokenModel tokenModel = new TokenModel(userId, token);
        redisTemplate.boundValueOps(userId).set(token, Constants.TOKEN_EXPIRES_DAYS, TimeUnit.DAYS);
        return tokenModel;
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

        // 验证成功延长过期时间
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

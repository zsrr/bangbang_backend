package com.stephen.bangbang.base.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

@Component("tokenManager")
public class TokenManagerImpl implements TokenManager {
    private JedisPool jedisPool;

    private static final String TOKEN_KEY_SUFFIX = "-token";

    @Autowired
    public TokenManagerImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private String getTokenKey(Long userId) {
        return userId + TOKEN_KEY_SUFFIX;
    }

    @Override
    public TokenModel createToken(Long userId) {
        try(Jedis jedis = jedisPool.getResource()) {
            final String token = userId + "-" + UUID.randomUUID().toString().replace("-", "");
            jedis.set(getTokenKey(userId), token);
            jedis.expire(getTokenKey(userId), 7 * 24 * 60 * 60);
            return new TokenModel(userId, token);
        }
    }

    @Override
    public boolean checkToken(TokenModel tokenModel) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (tokenModel == null ||
                    tokenModel.getUserId() == null ||
                    tokenModel.getToken() == null) {
                return false;
            }
            String token = jedis.get(getTokenKey(tokenModel.getUserId()));
            if (token == null || !token.equals(tokenModel.getToken())) {
                return false;
            }
            jedis.expire(getTokenKey(tokenModel.getUserId()), 7 * 24 * 60 * 60);
            return true;
        }
    }

    @Override
    public TokenModel getToken(Long userId) {
        try(Jedis jedis = jedisPool.getResource()) {
            if (userId == null)
                return null;
            String token = jedis.get(getTokenKey(userId));

            if (token == null)
                return null;

            return new TokenModel(userId, token);
        }
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
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.del(getTokenKey(userId));
        }
    }
}

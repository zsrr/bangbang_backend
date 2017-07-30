package com.stephen.bangbang.authorization;

import com.stephen.bangbang.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class TokenManagerImpl implements TokenManager {
    private RedisTemplate<Long, String> redisTemplate;

    @Autowired
    public TokenManagerImpl(RedisTemplate<Long, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // 感觉Redis事务不是支持的很好
    @Override
    public TokenModel createToken(Long userId) {
        final String token = userId + "-" + UUID.randomUUID().toString().replace("-", "");
        TokenModel tokenModel = redisTemplate.execute(new SessionCallback<TokenModel>() {
            @Override
            public <K, V> TokenModel execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                RedisOperations<Long, String> transformedOperations = (RedisOperations<Long, String>) redisOperations;
                transformedOperations.multi();
                BoundValueOperations<Long, String> boundValueOperations = transformedOperations.boundValueOps(userId);
                boundValueOperations.set(token, Constants.TOKEN_EXPIRES_DAYS, TimeUnit.DAYS);
                transformedOperations.exec();
                return new TokenModel(userId, token);
            }
        });
        return tokenModel;
    }

    @Override
    public boolean checkToken(TokenModel tokenModel) {
        if (tokenModel == null ||
                tokenModel.getUserId() == null ||
                tokenModel.getToken() == null) {
            return false;
        }

        String token = redisTemplate.execute(new SessionCallback<String>() {
            @Override
            public <K, V> String execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                RedisOperations<Long, String> transformedOps = (RedisOperations<Long, String>) redisOperations;
                transformedOps.multi();
                transformedOps.boundValueOps(tokenModel.getUserId()).get();
                // 事务不能立即执行
                return (String) transformedOps.exec().get(0);
            }
        });
        if (token == null || !token.equals(tokenModel.getToken())) {
            return false;
        }

        // 验证成功延长过期时间
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                RedisOperations<Long, String> transformedOps = (RedisOperations<Long, String>) redisOperations;
                transformedOps.multi();
                transformedOps.boundValueOps(tokenModel.getUserId()).expire(Constants.TOKEN_EXPIRES_DAYS, TimeUnit.DAYS);
                transformedOps.exec();
                return null;
            }
        });
        return true;
    }

    @Override
    public TokenModel getToken(Long userId) {
        if (userId == null)
            return null;
        String token = redisTemplate.execute(new SessionCallback<String>() {
            @Override
            public <K, V> String execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                RedisOperations<Long, String> transformedOps = (RedisOperations<Long, String>) redisOperations;
                transformedOps.multi();
                transformedOps.boundValueOps(userId).get();
                // Redis事务不能立即执行
                return (String) transformedOps.exec().get(0);
            }
        });

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
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                RedisOperations<Long, String> transformedOps = (RedisOperations<Long, String>) redisOperations;
                transformedOps.multi();
                transformedOps.delete(userId);
                transformedOps.exec();
                return null;
            }
        });
    }
}

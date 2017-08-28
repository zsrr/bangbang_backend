package com.stephen.bangbang.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

// 为了在测试环境下避开对JPush API的调用
public class JPushServiceVirtualImpl implements JPushService {

    private JedisPool jedisPool;

    public JPushServiceVirtualImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public boolean allopatricLogin(Long userId, String registrationId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String formerRegistrationId = jedis.get(userId + "-registrationId");
            jedis.set(userId + "-registrationId", registrationId);
            return formerRegistrationId != null && !formerRegistrationId.equals(registrationId);
        }
    }

    @Override
    public void updatePassword(Long userId) {

    }

    @Override
    public void makeFriendOnMake(Long userId, Long targetUserId) {

    }

    @Override
    public void makeFriendOnAgree(Long userId, Long targetUserId) {

    }
}

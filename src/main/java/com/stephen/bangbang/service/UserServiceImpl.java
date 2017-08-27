package com.stephen.bangbang.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stephen.bangbang.base.authorization.TokenManager;
import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.FriendsResponse;
import com.stephen.bangbang.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.Iterator;


@Service
public class UserServiceImpl implements UserService {
    private UserInfoRepository userDao;
    private TokenManager tokenManager;
    private JPushService jPushService;
    private JedisPool jedisPool;

    @Autowired
    public UserServiceImpl(UserInfoRepository userDao, TokenManager tokenManager, JedisPool jedisPool, JPushService jPushService) {
        this.jPushService = jPushService;
        this.userDao = userDao;
        this.tokenManager = tokenManager;
        this.jedisPool = jedisPool;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User register(String username, String password) {
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction tx = jedis.multi();
            tx.incrBy("user-count", 1);
            tx.get("user-count");
            Long count = (Long) tx.exec().get(0);
            return userDao.register(username, password, "User-" + count);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User login(Long id, String password, String registrationId) {
        User user = userDao.findUser(id);
        postFindUser(user, password, registrationId);
        return user;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User login(String username, String password, String registrationId) {
        User user = userDao.findUser(username);
        postFindUser(user, password, registrationId);
        return user;
    }

    private void postFindUser(User user, String password, String registrationId) {
        if (!user.getPassword().equals(password)) {
            throw new PasswordIncorrectException();
        }
        tokenManager.createToken(user.getId());
        jPushService.allopatricLogin(user.getId(), registrationId);
    }

    @Override
    public void logout(Long id) {
        tokenManager.deleteToken(id);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getUser(String username) {
        return userDao.findUser(username);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getUser(Long userId) {
        return userDao.findUser(userId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void update(Long userId, ObjectNode updatedNode) {
        User user = userDao.findUser(userId);
        try {
            user = merge(user, updatedNode);
            userDao.update(user);
            // 更改密码操作
            if (updatedNode.get("password") != null) {
                tokenManager.deleteToken(userId);
                jPushService.updatePassword(userId);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new JsonInvalidException(e);
        }
    }

    @Override
    public String getToken(Long userId) {
        return tokenManager.getToken(userId).getToken();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public FriendsResponse getFriends(Long userId) {
        return userDao.getFriends(userId);
    }

    @Override
    public void makeFriendOnMake(Long userId, Long targetUserId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd("make-friends-requests", userId + "-" + targetUserId);
            jPushService.makeFriendOnMake(userId, targetUserId);
        }
    }

    @Override
    public void makeFriendOnAgree(Long userId, Long targetUserId) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.sismember("make-friends-requests", targetUserId + "-" + userId)) {
                throw new NoMakingFriendsException();
            }

            userDao.makeFriend(userId, targetUserId);
            jedis.srem("make-friends-requests", targetUserId + "-" + userId);
            jPushService.makeFriendOnAgree(userId, targetUserId);
        }
    }

    private User merge(User targetUser, ObjectNode node) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode targetNode = objectMapper.convertValue(targetUser, ObjectNode.class);
        targetNode.put("password", targetUser.getPassword());

        Iterator<String> fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            JsonNode updateValue = node.get(field);
            targetNode.replace(field, updateValue);
        }

        return objectMapper.treeToValue(targetNode, User.class);
    }
}

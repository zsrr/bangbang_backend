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
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;


@Service
public class UserServiceImpl implements UserService {
    private UserInfoRepository userDao;
    private TokenManager tokenManager;
    private RedisTemplate<String, String> redisTemplate;
    private JPushService jPushService;

    @Autowired
    public UserServiceImpl(UserInfoRepository userDao, TokenManager tokenManager, RedisTemplate<String, String> redisTemplate, JPushService jPushService) {
        this.jPushService = jPushService;
        this.userDao = userDao;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public User register(String username, String password) {
        User user = userDao.findUser(username);
        if (user != null) {
            throw new DuplicatedUserException();
        }
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps("user-count");
        ops.increment(1);
        String count = ops.get();
        user = userDao.register(username, password, "User-" + count);
        return user;
    }

    @Override
    public User login(Long id, String password, String registrationId) {
        User user = userDao.findUser(id);
        postFindUser(user, password, registrationId);
        return user;
    }

    @Override
    public User login(String username, String password, String registrationId) {
        User user = userDao.findUser(username);
        postFindUser(user, password, registrationId);
        return user;
    }

    private void postFindUser(User user, String password, String registrationId) {
        if (user == null) {
            throw new UserNotFoundException();
        }

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
    public User getUser(String username) {
        User user = userDao.findUser(username);
        if (user == null)
            throw new UserNotFoundException();
        return user;
    }

    @Override
    public User getUser(Long userId) {
        User user = userDao.findUser(userId);
        if (user == null)
            throw new UserNotFoundException();
        return user;
    }

    @Override
    public void update(Long userId, ObjectNode updatedNode) {
        User user = userDao.findUser(userId);
        if (user == null)
            throw new UserNotFoundException();
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
    public FriendsResponse getFriends(Long userId) {
        return userDao.getFriends(userId);
    }

    @Override
    public void makeFriendOnMake(Long userId, Long targetUserId) {
        if (!userDao.hasUser(userId) || !userDao.hasUser(targetUserId)) {
            throw new UserNotFoundException();
        }

        jPushService.makeFriendOnMake(userId, targetUserId);
        redisTemplate.boundSetOps("make-friends-requests").add(userId + "-" + targetUserId);
    }

    @Override
    public void makeFriendOnAgree(Long userId, Long targetUserId) {
        if (!userDao.hasUser(userId) || !userDao.hasUser(targetUserId)) {
            throw new UserNotFoundException();
        }

        if (!redisTemplate.boundSetOps("make-friends-requests").isMember(targetUserId + "-" + userId)) {
            throw new NoMakingFriendsException();
        }

        userDao.makeFriend(userId, targetUserId);
        jPushService.makeFriendOnAgree(userId, targetUserId);
        redisTemplate.boundSetOps("make-friends-requests").remove(targetUserId + "-" + userId);
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

package com.stephen.bangbang.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stephen.bangbang.authorization.TokenManager;
import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.JsonInvalidException;
import com.stephen.bangbang.exception.user.DuplicatedUserException;
import com.stephen.bangbang.exception.user.PasswordIncorrectException;
import com.stephen.bangbang.exception.user.UserNotFoundException;
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

    @Autowired
    public UserServiceImpl(UserInfoRepository userDao, TokenManager tokenManager, RedisTemplate<String, String> redisTemplate) {
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
    public User login(Long id, String password) {
        User user = userDao.findUser(id);
        postFindUser(user, password);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userDao.findUser(username);
        postFindUser(user, password);
        return user;
    }

    private void postFindUser(User user, String password) {
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.getPassword().equals(password)) {
            throw new PasswordIncorrectException();
        }
        tokenManager.createToken(user.getId());
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
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new JsonInvalidException(e);
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

    public TokenManager getTokenManager() {
        return tokenManager;
    }
}

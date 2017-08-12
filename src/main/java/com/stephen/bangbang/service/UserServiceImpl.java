package com.stephen.bangbang.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stephen.bangbang.authorization.TokenManager;
import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.JPushException;
import com.stephen.bangbang.exception.JsonInvalidException;
import com.stephen.bangbang.exception.DuplicatedUserException;
import com.stephen.bangbang.exception.PasswordIncorrectException;
import com.stephen.bangbang.exception.UserNotFoundException;
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
    private JPushClient jPushClient;

    @Autowired
    public UserServiceImpl(UserInfoRepository userDao, TokenManager tokenManager, RedisTemplate<String, String> redisTemplate, JPushClient jPushClient) {
        this.jPushClient = jPushClient;
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
        allopatricLogin(user.getId(), registrationId);
    }

    private void allopatricLogin(Long userId, String registrationId) {
        BoundValueOperations<String, String> ops = redisTemplate.boundValueOps("" + userId);
        String formerRegistrationId = ops.get();
        if (formerRegistrationId == null || formerRegistrationId.equals("")) {
            ops.set(formerRegistrationId);
        } else if (!formerRegistrationId.equals(registrationId)) {
            PushPayload pushPayload = getAllopatricLoginPayload(formerRegistrationId);
            try {
                jPushClient.sendPush(pushPayload);
                ops.set(registrationId);
            } catch (APIConnectionException | APIRequestException e) {
                throw new JPushException(e);
            }
        }
    }

    private PushPayload getAllopatricLoginPayload(String destination) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(destination))
                .setNotification(Notification.newBuilder()
                        .setAlert("存在异地登录，请检查账号密码是否被更改")
                        .addPlatformNotification(
                                AndroidNotification.newBuilder()
                                        .setTitle("异地登录").build())
                        .addPlatformNotification(
                                IosNotification.newBuilder()
                                        .incrBadge(1).
                                        build()).build()).
                        build();
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

package com.stephen.bangbang.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.stephen.bangbang.Constants;
import com.stephen.bangbang.exception.JPushException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JPushServiceImpl implements JPushService {
    private JedisPool jedisPool;
    private JPushClient client;

    private static final String REGISTRATION_SUFFIX = "-registrationId";

    private String getRegistrationKey(Long userId) {
        return userId + REGISTRATION_SUFFIX;
    }

    @Autowired
    public JPushServiceImpl(JedisPool jedisPool, JPushClient client) {
        this.jedisPool = jedisPool;
        this.client = client;
    }

    @Override
    public void allopatricLogin(Long userId, String registrationId) {
        try(Jedis jedis = jedisPool.getResource()) {
            String formerRegistrationId = jedis.get(getRegistrationKey(userId));
            jedis.set(getRegistrationKey(userId), registrationId);
            if (formerRegistrationId != null && !formerRegistrationId.equals(registrationId)) {
                PushPayload pushPayload = getAllopatricLoginPayload(formerRegistrationId);
                try {
                    client.sendPush(pushPayload);
                } catch (APIConnectionException | APIRequestException e) {
                    throw new JPushException(e);
                }
            }
        }
    }

    private PushPayload getAllopatricLoginPayload(String destination) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(destination))
                .setMessage(Message.newBuilder()
                        .setContentType(Constants.JPUSH_MESSAGE_ALLOPATRIC_LOGIN)
                        .setTitle("已在别处登录")
                        .setMsgContent("用户已在别地登录，请检查账号密码是否泄露")
                        .build())
                .build();
    }

    @Override
    public void updatePassword(Long userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String registrationId = jedis.get(getRegistrationKey(userId));
            if (registrationId != null && !registrationId.equals("")) {
                try {
                    client.sendPush(passwordChangedPushPayload(registrationId));
                } catch (APIConnectionException | APIRequestException e) {
                    throw new JPushException(e);
                }
            }
        }
    }

    private PushPayload passwordChangedPushPayload(String registrationId) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(registrationId))
                .setMessage(Message.newBuilder()
                        .setTitle("密码已更改")
                        .setMsgContent("您的密码已经更改，请重新登录")
                        .setContentType(Constants.JPUSH_MESSAGE_PASSWORD_CHANGED)
                        .build())
                .build();
    }

    private PushPayload makeFriendPushPayload(Long userId, Long targetUserId, String registrationId) {
        if (registrationId != null && !registrationId.equals(""))
            return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(registrationId))
                .setMessage(Message.newBuilder()
                        .setTitle("好友请求")
                        .setContentType(Constants.JPUSH_MESSAGE_MAKE_FRIEND)
                        .setMsgContent("收到新的好友请求")
                        .addExtra("userId", "" + userId)
                        .build())
                .setNotification(Notification.newBuilder()
                        .setAlert("收到新的好友请求")
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle("收到新的好友通知")
                                .addExtra("userId", "" + userId)
                                .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .addExtra("userId", "" + userId)
                                .build())
                        .build())
                .build();
        return null;
    }

    private PushPayload agreeOnFriendsRequestPushPayload(Long userId, Long targetUserId, String registrationId) {
        if (registrationId != null)
            return PushPayload.newBuilder()
                    .setPlatform(Platform.all())
                    .setAudience(Audience.registrationId(registrationId))
                    .setMessage(Message.newBuilder()
                            .setTitle("好友请求已接受")
                            .setContentType(Constants.JPUSH_MESSAGE_MAKE_FRIEND_AGREE)
                            .setMsgContent("已通过您的请求")
                            .addExtra("userId", "" + userId)
                            .build())
                    .setNotification(Notification.newBuilder()
                            .setAlert("好友请求已接受")
                            .addPlatformNotification(AndroidNotification.newBuilder()
                                    .setTitle("已通过您的请求")
                                    .addExtra("userId", "" + userId)
                                    .build())
                            .addPlatformNotification(IosNotification.newBuilder()
                                    .addExtra("userId", "" + userId)
                                    .build())
                            .build())
                    .build();
        return null;
    }

    @Override
    public void makeFriendOnMake(Long userId, Long targetUserId) {
        try (Jedis jedis = jedisPool.getResource()) {
            client.sendPush(makeFriendPushPayload(userId, targetUserId, jedis.get(getRegistrationKey(targetUserId))));
        } catch (APIConnectionException | APIRequestException e) {
            throw new JPushException(e);
        }
    }

    @Override
    public void makeFriendOnAgree(Long userId, Long targetUserId) {
        try (Jedis jedis = jedisPool.getResource()) {
            client.sendPush(agreeOnFriendsRequestPushPayload(userId, targetUserId, jedis.get(getRegistrationKey(targetUserId))));
        } catch (APIConnectionException | APIRequestException e) {
            throw new JPushException(e);
        }
    }
}

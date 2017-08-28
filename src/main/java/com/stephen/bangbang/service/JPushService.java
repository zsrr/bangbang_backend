package com.stephen.bangbang.service;

public interface JPushService {
    boolean allopatricLogin(Long userId, String registrationId);
    void updatePassword(Long userId);
    void makeFriendOnMake(Long userId, Long targetUserId);
    void makeFriendOnAgree(Long userId, Long targetUserId);
}

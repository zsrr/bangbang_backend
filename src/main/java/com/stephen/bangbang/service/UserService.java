package com.stephen.bangbang.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.FriendsResponse;

public interface UserService {
    User register(String username, String password);

    User login(Long id, String password, String registrationId);

    User login(String username, String password, String registrationId);

    void logout(Long id);

    User getUser(String username);

    User getUser(Long userId);

    void update(Long userId, ObjectNode updatedNode);

    String getToken(Long userId);

    FriendsResponse getFriends(Long userId);

    void makeFriendOnMake(Long userId, Long targetUserId);

    void makeFriendOnAgree(Long userId, Long targetUserId);
}

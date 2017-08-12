package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.FriendsResponse;

public interface UserInfoRepository {
    User findUser(String name);
    User findUser(Long id);
    User register(String name, String password, String nickname);
    void update(User updatedUser);
    boolean hasUser(Long id);
    boolean hasUser(String username);
    FriendsResponse getFriends(Long userId);
}

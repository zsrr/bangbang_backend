package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.User;

public interface UserInfoRepository {
    User findUser(String name);
    User findUser(Long id);
    User register(String name, String password, String nickname);
}

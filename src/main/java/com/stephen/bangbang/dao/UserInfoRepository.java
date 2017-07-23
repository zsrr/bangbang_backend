package com.stephen.bangbang.dao;

import com.stephen.bangbang.domain.User;

public interface UserInfoRepository {
    User findUser(String name);
    void register(String name, String password);
}

package com.stephen.bangbang.service;

import com.stephen.bangbang.domain.User;

public interface UserService {
    User register(String username, String password);

    User login(Long id, String password);

    User login(String username, String password);

    void logout(Long id);
}

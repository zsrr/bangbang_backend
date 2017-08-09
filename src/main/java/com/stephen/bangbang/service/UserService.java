package com.stephen.bangbang.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stephen.bangbang.domain.User;

public interface UserService {
    User register(String username, String password);

    User login(Long id, String password);

    User login(String username, String password);

    void logout(Long id);

    User getUser(String username);

    User getUser(Long userId);

    void update(Long userId, ObjectNode updatedNode);
}

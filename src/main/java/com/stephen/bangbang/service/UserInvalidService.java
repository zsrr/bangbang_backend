package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.exception.UserNotFoundException;

public interface UserInvalidService {
    default void invalidUser(Long userId, UserInfoRepository userDAO) {
        if (!userDAO.hasUser(userId)) {
            throw new UserNotFoundException();
        }
    }
    default void invalidUser(String username, UserInfoRepository userDAO) {
        if (!userDAO.hasUser(username)) {
            throw new UserNotFoundException();
        }
    }
}

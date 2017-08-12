package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.UserNotFoundException;

public interface UserInvalidService {
    default void invalidUser(Long userId, UserInfoRepository userDAO) {
        User user = userDAO.findUser(userId);
        if (user == null)
            throw new UserNotFoundException();
    }
    default void invalidUser(String username, UserInfoRepository userDAO) {
        User user = userDAO.findUser(username);
        if (user == null)
            throw new UserNotFoundException();
    }
}

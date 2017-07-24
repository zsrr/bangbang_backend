package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.user.DuplicatedUserException;
import com.stephen.bangbang.exception.user.PasswordIncorrectException;
import com.stephen.bangbang.exception.user.UserInfoInvalidException;
import com.stephen.bangbang.exception.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserInfoRepository userDao;

    @Autowired
    public UserService(UserInfoRepository userDao) {
        this.userDao = userDao;
    }

    public void register(String username, String password) {
        if (username == null || username.length() < 5 || username.length() > 16 || password == null) {
            throw new UserInfoInvalidException();
        }

        User user = userDao.findUser(username);
        if (user != null) {
            throw new DuplicatedUserException();
        }

        userDao.register(username, password);
    }

    public User getUser(String username, String password) {
        User user = userDao.findUser(username);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.getPassword().equals(password)) {
            throw new PasswordIncorrectException();
        }

        return user;
    }
}

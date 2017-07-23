package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.DuplicatedUserException;
import com.stephen.bangbang.exception.PasswordIncorrectException;
import com.stephen.bangbang.exception.UserInfoInvalidException;
import com.stephen.bangbang.exception.UserNotFoundException;
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
        if (username == null || password == null) {
            throw new UserInfoInvalidException();
        }

        User user = userDao.findUser(username);
        if (user != null) {
            throw new DuplicatedUserException();
        }

        userDao.register(username, password);
    }

    public User login(String username, String password) {
        User user = userDao.findUser(username);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.getPassword().equals(password)) {
            throw new PasswordIncorrectException();
        }

        return user;
    }

    public User findUser(String username) {
        return userDao.findUser(username);
    }
}

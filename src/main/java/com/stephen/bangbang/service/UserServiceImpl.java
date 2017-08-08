package com.stephen.bangbang.service;

import com.stephen.bangbang.authorization.TokenManager;
import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.user.DuplicatedUserException;
import com.stephen.bangbang.exception.user.PasswordIncorrectException;
import com.stephen.bangbang.exception.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserInfoRepository userDao;
    private TokenManager tokenManager;

    @Autowired
    public UserServiceImpl(UserInfoRepository userDao, TokenManager tokenManager) {
        this.userDao = userDao;
        this.tokenManager = tokenManager;
    }

    @Override
    public User register(String username, String password) {
        User user = userDao.findUser(username);
        if (user != null) {
            throw new DuplicatedUserException();
        }

        return user;
    }

    @Override
    public User login(Long id, String password) {
        User user = userDao.findUser(id);
        postFindUser(user, password);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userDao.findUser(username);
        postFindUser(user, password);
        return user;
    }

    private void postFindUser(User user, String password) {
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.getPassword().equals(password)) {
            throw new PasswordIncorrectException();
        }
        tokenManager.createToken(user.getId());
    }

    @Override
    public void logout(Long id) {
        tokenManager.deleteToken(id);
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }
}

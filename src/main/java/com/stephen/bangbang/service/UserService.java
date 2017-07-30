package com.stephen.bangbang.service;

import com.stephen.bangbang.domain.User;

public interface UserService extends UserInvalidService {
    User register(String username, String password);

    User login(Long id, String password);

    User login(String username, String password);

    /*private void postFindUser(User user, String password) {
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.getPassword().equals(password)) {
            throw new PasswordIncorrectException();
        }
        tokenManager.createToken(user.getId());
    }*/

    void logout(Long id);
}

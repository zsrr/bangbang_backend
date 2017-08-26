package com.stephen.bangbang.service;

import com.stephen.bangbang.domain.User;

public interface UserValidationService {
    void invalidUser(Long userId);
    void invalidUser(String username);
    void isCurrentUser(Long userId, Long currentUserId);
    void registerValidation(User user);
}

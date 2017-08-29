package com.stephen.bangbang.service;

import com.stephen.bangbang.dao.UserInfoRepository;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.exception.DuplicatedUserException;
import com.stephen.bangbang.exception.NotCurrentUserException;
import com.stephen.bangbang.exception.UserInfoInvalidException;
import com.stephen.bangbang.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserValidationServiceImpl implements UserValidationService {

    private UserInfoRepository userDAO;

    @Autowired
    public UserValidationServiceImpl(UserInfoRepository userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void invalidUser(Long userId) {
        if (!userDAO.hasUser(userId)) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public void invalidUser(String username) {
        if (!userDAO.hasUser(username)) {
            throw new UserNotFoundException();
        }
    }

    @Override
    public void isCurrentUser(Long userId, Long currentUserId) {
        if (!userId.equals(currentUserId)) {
            throw new NotCurrentUserException();
        }
    }

    @Override
    public void registerValidation(User user) {

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<User>> usernameViolations = validator.validateProperty(user, "username");
        Set<ConstraintViolation<User>> passwordViolations = validator.validateProperty(user, "password");

        if (!usernameViolations.isEmpty() ||
                !passwordViolations.isEmpty()) {
            throw new UserInfoInvalidException();
        }

        if (userDAO.hasUser(user.getUsername())) {
            throw new DuplicatedUserException();
        }
    }

}

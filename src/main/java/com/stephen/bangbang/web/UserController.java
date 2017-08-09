package com.stephen.bangbang.web;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stephen.bangbang.authorization.Authorization;
import com.stephen.bangbang.authorization.CurrentUserId;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.TasksResponse;
import com.stephen.bangbang.dto.UserLoginResponse;
import com.stephen.bangbang.dto.UserRegisterResponse;
import com.stephen.bangbang.exception.task.TaskInfoInvalidException;
import com.stephen.bangbang.exception.user.NotCurrentUserException;
import com.stephen.bangbang.exception.user.UserInfoInvalidException;
import com.stephen.bangbang.service.TaskService;
import com.stephen.bangbang.service.UserService;
import com.stephen.bangbang.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;
    private final ValidatorFactory validatorFactory;

    @Autowired
    public UserController(UserService userService, TaskService taskService, ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
        this.userService = userService;
        this.taskService = taskService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<UserRegisterResponse> register(@RequestBody User postUser) {
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<User>> usernameViolations = validator.validateProperty(postUser, "username");
        Set<ConstraintViolation<User>> passwordViolations = validator.validateProperty(postUser, "password");

        if (!usernameViolations.isEmpty() ||
                !passwordViolations.isEmpty()) {
            throw new UserInfoInvalidException();
        }

        User user = userService.register(postUser.getUsername(), postUser.getPassword());
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse(user.getId());
        userRegisterResponse.setStatus(HttpStatus.CREATED.value());
        return new ResponseEntity<UserRegisterResponse>(userRegisterResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userIdentifier}", method = RequestMethod.GET)
    public ResponseEntity<UserLoginResponse> login(@PathVariable(value = "userIdentifier") String identifier, @RequestHeader("password") String password) {
        User user;
        try {
            Long id = Long.parseLong(identifier);
            user = userService.login(id, password);
        } catch (NumberFormatException e) {
            user = userService.login(identifier, password);
        }
        UserLoginResponse userLoginResponse = new UserLoginResponse(user, ((UserServiceImpl) userService).getTokenManager().getToken(user.getId()).getToken());
        return new ResponseEntity<UserLoginResponse>(userLoginResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/baseInfo", method = RequestMethod.GET)
    @Authorization
    public ResponseEntity<User> getUserInfo(@PathVariable(value = "userId") Long userId) {
        User user = userService.getUser(userId);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    @Authorization
    public ResponseEntity<BaseResponse> updateUser(@PathVariable("userId") Long userId, @CurrentUserId Long currentUserId, @RequestBody ObjectNode updatedBody) {
        if (!userId.equals(currentUserId)) {
            throw new NotCurrentUserException();
        }
        userService.update(userId, updatedBody);
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @Authorization
    public ResponseEntity<BaseResponse> logout(@PathVariable("userId") Long userId, @CurrentUserId Long currentUserId) {
        if (!userId.equals(currentUserId)) {
            throw new NotCurrentUserException();
        }
        userService.logout(userId);
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/tasks", method = RequestMethod.GET)
    @Authorization
    public ResponseEntity<TasksResponse> tasksByUserId(@PathVariable("userId") Long userId,
                                       @RequestParam(value = "lastTaskId", defaultValue = "0") Long lastTaskId,
                                       @RequestParam(value = "numberPerPage", defaultValue = "5") int numberPerPage) {
        return new ResponseEntity<TasksResponse>(taskService.getAllTasksByUser(userId, lastTaskId, numberPerPage), HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/tasks", method = RequestMethod.POST)
    @Authorization
    public ResponseEntity<BaseResponse> publish(@PathVariable("userId") Long userId, @RequestBody @Valid HelpingTask ht, BindingResult bindingResult, @CurrentUserId Long currentUserId) {
        if (!userId.equals(currentUserId)) {
            throw new NotCurrentUserException();
        }

        if (bindingResult.hasErrors()) {
            throw new TaskInfoInvalidException();
        }

        taskService.publish(userId, ht);

        return new ResponseEntity<BaseResponse>(new BaseResponse(HttpStatus.CREATED, null), HttpStatus.CREATED);
    }

}

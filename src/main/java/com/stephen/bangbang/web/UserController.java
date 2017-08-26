package com.stephen.bangbang.web;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stephen.bangbang.Constants;
import com.stephen.bangbang.base.authorization.Authorization;
import com.stephen.bangbang.base.annotation.CurrentUserId;
import com.stephen.bangbang.domain.HelpingTask;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.*;
import com.stephen.bangbang.exception.ActionResolveException;
import com.stephen.bangbang.exception.TaskInfoInvalidException;
import com.stephen.bangbang.exception.NotCurrentUserException;
import com.stephen.bangbang.exception.UserInfoInvalidException;
import com.stephen.bangbang.service.TaskService;
import com.stephen.bangbang.service.TaskValidationService;
import com.stephen.bangbang.service.UserValidationService;
import com.stephen.bangbang.service.UserService;
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
    private final UserValidationService userValidationService;
    private final TaskValidationService taskValidationService;

    @Autowired
    public UserController(UserService userService, TaskService taskService, UserValidationService userValidationService, TaskValidationService taskValidationService) {
        this.userService = userService;
        this.taskService = taskService;
        this.userValidationService = userValidationService;
        this.taskValidationService = taskValidationService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<UserRegisterResponse> register(@RequestBody User postUser) {
        userValidationService.registerValidation(postUser);
        User user = userService.register(postUser.getUsername(), postUser.getPassword());
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse(user.getId());
        userRegisterResponse.setStatus(HttpStatus.CREATED.value());
        return new ResponseEntity<UserRegisterResponse>(userRegisterResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userIdentifier}", method = RequestMethod.GET)
    public ResponseEntity<UserLoginResponse> login(@PathVariable(value = "userIdentifier") String identifier, @RequestHeader("password") String password, @RequestHeader("registrationId") String registrationId) {
        User user;
        try {
            Long id = Long.parseLong(identifier);
            userValidationService.invalidUser(id);
            user = userService.login(id, password, registrationId);
        } catch (NumberFormatException e) {
            userValidationService.invalidUser(identifier);
            user = userService.login(identifier, password, registrationId);
        }
        UserLoginResponse userLoginResponse = new UserLoginResponse(user, userService.getToken(user.getId()));
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
        userValidationService.isCurrentUser(userId, currentUserId);
        userService.update(userId, updatedBody);
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @Authorization
    public ResponseEntity<BaseResponse> logout(@PathVariable("userId") Long userId, @CurrentUserId Long currentUserId) {
        userValidationService.isCurrentUser(userId, currentUserId);
        userService.logout(userId);
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/tasks", method = RequestMethod.GET)
    @Authorization
    public ResponseEntity<TasksResponse> tasksByUserId(@PathVariable("userId") Long userId,
                                       @RequestParam(value = "lastTaskId", defaultValue = "0") Long lastTaskId,
                                       @RequestParam(value = "numberPerPage", defaultValue = "5") int numberPerPage) {
        userValidationService.invalidUser(userId);
        taskValidationService.invalidTask(lastTaskId);
        return new ResponseEntity<TasksResponse>(taskService.getAllTasksByUser(userId, lastTaskId, numberPerPage), HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/tasks", method = RequestMethod.POST)
    @Authorization
    public ResponseEntity<BaseResponse> publish(@PathVariable("userId") Long userId, @RequestBody @Valid HelpingTask ht, BindingResult bindingResult, @CurrentUserId Long currentUserId) {
        userValidationService.isCurrentUser(userId, currentUserId);

        if (bindingResult.hasErrors()) {
            throw new TaskInfoInvalidException();
        }
        taskService.publish(userId, ht);
        return new ResponseEntity<BaseResponse>(new BaseResponse(HttpStatus.CREATED, null), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userId}/friends", method = RequestMethod.GET)
    @Authorization
    public ResponseEntity<FriendsResponse> friends(@PathVariable("userId") Long userId, @CurrentUserId Long currentUserId) {
        userValidationService.isCurrentUser(userId, currentUserId);
        FriendsResponse response = userService.getFriends(userId);
        return new ResponseEntity<FriendsResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/friends", method = RequestMethod.POST)
    @Authorization
    public ResponseEntity<BaseResponse> makeFriend(@PathVariable("userId") Long userId,
                                                   @CurrentUserId Long currentUserId,
                                                   @RequestParam("action") String action,
                                                   @RequestParam(value = "targetUserId") Long targetUserId) {
        userValidationService.isCurrentUser(userId, currentUserId);
        userValidationService.invalidUser(targetUserId);
        if (action.equals(Constants.FRIENDS_MAKE)) {
            userService.makeFriendOnMake(userId, targetUserId);
        } else if (action.equals(Constants.FRIENDS_AGREE)) {
            userService.makeFriendOnAgree(userId, targetUserId);
        } else {
            throw new ActionResolveException("名为" + action + "的动作无法解析");
        }

        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/recentlyFinishedTasks", method = RequestMethod.GET)
    @Authorization
    public ResponseEntity<TasksResponse> recentlyFinishedTasks(@PathVariable("userId") Long userId) {
        userValidationService.invalidUser(userId);
        return new ResponseEntity<TasksResponse>(taskService.getTasksRecentlyFinished(userId), HttpStatus.OK);
    }
}

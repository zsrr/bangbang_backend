package com.stephen.bangbang.web;

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

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    @Autowired
    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid User postUser, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
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

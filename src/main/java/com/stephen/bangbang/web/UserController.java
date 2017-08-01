package com.stephen.bangbang.web;

import com.stephen.bangbang.authorization.Authorization;
import com.stephen.bangbang.authorization.CurrentUser;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.UserLoginResponse;
import com.stephen.bangbang.dto.UserRegisterResponse;
import com.stephen.bangbang.exception.user.NotCurrentUserException;
import com.stephen.bangbang.service.UserService;
import com.stephen.bangbang.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<UserRegisterResponse> register(@RequestHeader("username") String username, @RequestHeader("password") String password) {
        User user = userService.register(username, password);
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
    public ResponseEntity<BaseResponse> logout(@PathVariable("userId") Long userId, @CurrentUser User user) {
        if (!user.getId().equals(userId)) {
            throw new NotCurrentUserException();
        }
        userService.logout(user.getId());
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }
}

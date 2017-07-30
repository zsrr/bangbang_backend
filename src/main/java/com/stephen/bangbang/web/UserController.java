package com.stephen.bangbang.web;

import com.stephen.bangbang.authorization.Authorization;
import com.stephen.bangbang.authorization.CurrentUser;
import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.UserLoginResponse;
import com.stephen.bangbang.dto.UserRegisterResponse;
import com.stephen.bangbang.exception.user.UserInfoInvalidException;
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<UserRegisterResponse> register(@RequestHeader("username") String username, @RequestHeader("password") String password) {
        User user = userService.register(username, password);
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse(user.getId());
        userRegisterResponse.setStatus(HttpStatus.CREATED.value());
        return new ResponseEntity<UserRegisterResponse>(userRegisterResponse, HttpStatus.CREATED);
    }

    // 两种登录方式
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<UserLoginResponse> login(@RequestParam(value = "id", required = false) String id, @RequestParam(value = "username", required = false) String username, @RequestHeader("password") String password) {
        User user = null;
        if (id != null) {
            user = userService.login(Long.parseLong(id), password);
        } else if (username != null) {
            user = userService.login(username, password);
        } else {
            throw new UserInfoInvalidException();
        }
        UserLoginResponse userLoginResponse = new UserLoginResponse(user, ((UserServiceImpl) userService).getTokenManager().getToken(user.getId()).getToken());
        return new ResponseEntity<UserLoginResponse>(userLoginResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    @Authorization
    public ResponseEntity<BaseResponse> logout(@CurrentUser User user) {
        userService.logout(user.getId());
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }

    // 之后再进行设置
}

package com.stephen.bangbang.web;

import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.UserResponse;
import com.stephen.bangbang.service.UserService;
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
    public ResponseEntity<UserResponse> register(@RequestHeader("username") String username, @RequestHeader("password") String password) {
        User user = userService.register(username, password);
        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), null);
        userResponse.setStatus(HttpStatus.CREATED.value());
        return new ResponseEntity<UserResponse>(userResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> login(@RequestParam("id") String id, @RequestHeader("password") String password) {
        User user = userService.login(Long.parseLong(id), password);
        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), userService.getTokenManager().getToken(user.getId()).getToken());
        return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse> logout(@RequestParam("id") String id) {
        userService.logout(Long.parseLong(id));
        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }
}

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

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> register(@RequestHeader("username") String username, @RequestHeader("password") String password) {
        userService.register(username, password);
        return new ResponseEntity<BaseResponse>(new BaseResponse(HttpStatus.CREATED, null), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> getUserByUserName(@PathVariable("username") String username, @RequestHeader("password") String password) {
        User user = userService.getUser(username, password);
        UserResponse userResponse = new UserResponse(user.getUsername());
        return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);
    }
}

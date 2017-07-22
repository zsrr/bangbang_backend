package com.stephen.bangbang.web;

import com.stephen.bangbang.domain.User;
import com.stephen.bangbang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    private UserService loginService;

    @Autowired
    public LoginController(UserService loginService) {
        this.loginService = loginService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public User login(@RequestHeader("username") String userName,
                      @RequestHeader("password") String password) {
        return loginService.login(userName, password);
    }
}

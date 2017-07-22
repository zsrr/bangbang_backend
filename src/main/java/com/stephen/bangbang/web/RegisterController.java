package com.stephen.bangbang.web;

import com.stephen.bangbang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private UserService registerService;

    @Autowired
    public RegisterController(UserService registerService) {
        this.registerService = registerService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void register(@RequestHeader("username") String userName,
                         @RequestHeader("password") String password) {
        registerService.register(userName, password);
    }

}

package org.mycompany.controller;

import org.mycompany.service.exception.UserServiceException;
import org.mycompany.service.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/addMoney")
    public String addMoney(@RequestParam String userName, @RequestParam int countMoney) throws UserServiceException {
        return userService.addMoney(userName, countMoney);
    }

    @GetMapping("/getMoney")
    public String getMoney(@RequestParam String userName, @RequestParam int countMoney) throws UserServiceException {
        return userService.getMoney(userName, countMoney);
    }
}

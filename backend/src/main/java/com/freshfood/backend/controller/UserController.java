package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.entity.User;
import com.freshfood.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestParam String openid,
                                   @RequestParam(required = false) String nickName,
                                   @RequestParam(required = false) String avatarUrl) {
        if (nickName == null || nickName.isBlank()) {
            nickName = "微信用户";
        }

        if (avatarUrl == null) {
            avatarUrl = "";
        }

        return ApiResponse.success(userService.login(openid, nickName, avatarUrl));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @GetMapping("/openid/{openid}")
    public ApiResponse<User> getUserByOpenid(@PathVariable String openid) {
        return ApiResponse.success(userService.getUserByOpenid(openid));
    }

    @GetMapping
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.success(userService.listUsers());
    }
}
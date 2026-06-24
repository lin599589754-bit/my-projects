package com.freshfood.backend.controller;

import com.freshfood.backend.common.ApiResponse;
import com.freshfood.backend.dto.LoginResponse;
import com.freshfood.backend.dto.UserLoginRequest;
import com.freshfood.backend.entity.User;
import com.freshfood.backend.security.CurrentUser;
import com.freshfood.backend.security.JwtService;
import com.freshfood.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CurrentUser currentUser;

    public UserController(UserService userService, JwtService jwtService, CurrentUser currentUser) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.currentUser = currentUser;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @ModelAttribute UserLoginRequest userLoginRequest) {
        String openid = userLoginRequest.getOpenid();
        String nickName = userLoginRequest.getNickName();
        String avatarUrl = userLoginRequest.getAvatarUrl();

        if (nickName == null || nickName.isBlank()) {
            nickName = "微信用户";
        }

        if (avatarUrl == null) {
            avatarUrl = "";
        }

        User user = userService.login(openid, nickName, avatarUrl);
        String token = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse("Bearer", token, jwtService.getExpiresIn(), user);

        return ApiResponse.success(loginResponse);
    }

    @GetMapping("/current")
    public ApiResponse<User> getCurrentUser() {
        return ApiResponse.success(userService.getUserById(currentUser.getUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable @Min(value = 1, message = "用户ID不能小于1") Long id) {
        currentUser.requireSameUser(id);
        return ApiResponse.success(userService.getUserById(id));
    }

    @GetMapping("/openid/{openid}")
    public ApiResponse<User> getUserByOpenid(@PathVariable
                                             @NotBlank(message = "openid不能为空")
                                             @Size(max = 64, message = "openid长度不能超过64")
                                             String openid) {
        return ApiResponse.success(userService.getUserByOpenid(openid));
    }

    @GetMapping
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.success(userService.listUsers());
    }
}

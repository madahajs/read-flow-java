package com.qizhi.readflow.controller;

import com.qizhi.readflow.common.Result;
import com.qizhi.readflow.dto.LoginRequest;
import com.qizhi.readflow.dto.RegisterRequest;
import com.qizhi.readflow.entity.User;
import com.qizhi.readflow.service.TokenBlacklistService;
import com.qizhi.readflow.service.UserService;
import com.qizhi.readflow.util.JwtUtil;
import com.qizhi.readflow.vo.LoginVO;
import com.qizhi.readflow.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword(), request.getUsername());
        return Result.success(UserVO.fromEntity(user));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getEmail(), request.getPassword());
        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        LoginVO loginVO = new LoginVO(token, UserVO.fromEntity(user));
        return Result.success(loginVO);
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.addToBlacklist(token);
        }
        return Result.success(null);
    }
}

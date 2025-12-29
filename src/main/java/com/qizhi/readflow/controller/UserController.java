package com.qizhi.readflow.controller;

import com.qizhi.readflow.common.Result;
import com.qizhi.readflow.service.UserService;
import com.qizhi.readflow.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息
     * TODO: 实际项目中应从 token 中解析用户ID
     */
    @GetMapping("/profile")
    public Result<UserVO> getProfile(@RequestHeader(value = "Authorization", required = false) String authorization) {
        // 模拟返回用户信息 (实际应从 token 中获取用户ID)
        // 这里返回 ID 为 2 的测试用户
        var user = userService.getById(2L);
        if (user == null) {
            return Result.unauthorized("请先登录");
        }
        return Result.success(UserVO.fromEntity(user));
    }
}

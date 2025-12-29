package com.qizhi.readflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qizhi.readflow.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据邮箱查询用户
     */
    User getByEmail(String email);

    /**
     * 用户注册
     */
    User register(String email, String password, String username);

    /**
     * 用户登录
     */
    User login(String email, String password);
}

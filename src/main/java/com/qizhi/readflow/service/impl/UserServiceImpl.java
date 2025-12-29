package com.qizhi.readflow.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qizhi.readflow.common.BusinessException;
import com.qizhi.readflow.entity.User;
import com.qizhi.readflow.mapper.UserMapper;
import com.qizhi.readflow.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByEmail(String email) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
    }

    @Override
    public User register(String email, String password, String username) {
        // 检查邮箱是否已存在
        if (getByEmail(email) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password));
        user.setUsername(username != null ? username : "User" + System.currentTimeMillis());
        user.setStatus(1);
        save(user);

        return user;
    }

    @Override
    public User login(String email, String password) {
        User user = getByEmail(email);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        return user;
    }
}

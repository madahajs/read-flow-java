package com.qizhi.readflow.vo;

import lombok.Data;

/**
 * 用户信息 VO
 */
@Data
public class UserVO {

    private String id;
    private String email;
    private String username;
    private String avatarUrl;

    public static UserVO fromEntity(com.qizhi.readflow.entity.User user) {
        UserVO vo = new UserVO();
        vo.setId("u_" + user.getId());
        vo.setEmail(user.getEmail());
        vo.setUsername(user.getUsername());
        vo.setAvatarUrl(user.getAvatarUrl());
        return vo;
    }
}

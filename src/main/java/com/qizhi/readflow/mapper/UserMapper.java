package com.qizhi.readflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qizhi.readflow.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}

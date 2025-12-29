package com.qizhi.readflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 语音配置实体
 */
@Data
@TableName("rf_voice")
public class Voice {

    /**
     * 语音ID (如 zh-CN-XiaoxiaoNeural)
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 语音名称
     */
    private String name;

    /**
     * 地区代码
     */
    private String region;

    /**
     * 语言代码
     */
    private String language;

    /**
     * 性别: Male/Female
     */
    private String gender;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 标签(JSON数组)
     */
    private String tags;

    /**
     * 状态: 0-禁用, 1-正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

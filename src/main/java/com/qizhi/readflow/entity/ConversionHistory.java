package com.qizhi.readflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 转换历史实体
 */
@Data
@TableName("rf_conversion_history")
public class ConversionHistory {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件标题
     */
    private String title;

    /**
     * 类型: upload/text
     */
    private String type;

    /**
     * 原始文本内容
     */
    private String originalText;

    /**
     * 使用的语音ID
     */
    private String voiceId;

    /**
     * 生成的音频URL
     */
    private String audioUrl;

    /**
     * 音频时长(秒)
     */
    private Integer audioDuration;

    /**
     * 状态: pending/processing/completed/failed
     */
    private String status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

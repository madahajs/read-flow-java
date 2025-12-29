-- Read Flow 数据库表结构
-- 创建数据库
-- CREATE DATABASE IF NOT EXISTS readflow DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE readflow;

-- 用户表
DROP TABLE IF EXISTS `rf_user`;
CREATE TABLE `rf_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 语音配置表
DROP TABLE IF EXISTS `rf_voice`;
CREATE TABLE `rf_voice` (
    `id` VARCHAR(100) NOT NULL COMMENT '语音ID (如 zh-CN-XiaoxiaoNeural)',
    `name` VARCHAR(50) NOT NULL COMMENT '语音名称',
    `region` VARCHAR(20) NOT NULL COMMENT '地区代码',
    `language` VARCHAR(20) NOT NULL COMMENT '语言代码',
    `gender` VARCHAR(10) NOT NULL COMMENT '性别: Male/Female',
    `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `tags` VARCHAR(200) DEFAULT NULL COMMENT '标签(JSON数组)',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='语音配置表';

-- 转换历史表
DROP TABLE IF EXISTS `rf_conversion_history`;
CREATE TABLE `rf_conversion_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文件标题',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: upload/text',
    `original_text` TEXT COMMENT '原始文本内容',
    `voice_id` VARCHAR(100) DEFAULT NULL COMMENT '使用的语音ID',
    `audio_url` VARCHAR(500) DEFAULT NULL COMMENT '生成的音频URL',
    `audio_duration` INT DEFAULT 0 COMMENT '音频时长(秒)',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/processing/completed/failed',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转换历史表';

package com.qizhi.readflow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qizhi.readflow.entity.ConversionHistory;

/**
 * 转换历史服务接口
 */
public interface ConversionHistoryService extends IService<ConversionHistory> {

    /**
     * 分页查询用户的转换历史
     */
    Page<ConversionHistory> pageByUserId(Long userId, Integer page, Integer pageSize);

    /**
     * 创建转换记录
     */
    ConversionHistory createHistory(Long userId, String title, String type, String text, String voiceId);
}

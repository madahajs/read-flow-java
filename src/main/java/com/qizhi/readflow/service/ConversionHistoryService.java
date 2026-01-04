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
     * 创建转换记录（初始状态为 pending）
     */
    ConversionHistory createHistory(Long userId, String title, String type, String text, String voiceId);

    /**
     * 更新转换记录为完成状态
     *
     * @param historyId     记录ID
     * @param audioUrl      音频URL
     * @param audioDuration 音频时长（秒）
     */
    void completeHistory(Long historyId, String audioUrl, Integer audioDuration);

    /**
     * 更新转换记录为失败状态
     *
     * @param historyId 记录ID
     * @param error     错误信息
     */
    void failHistory(Long historyId, String error);
}

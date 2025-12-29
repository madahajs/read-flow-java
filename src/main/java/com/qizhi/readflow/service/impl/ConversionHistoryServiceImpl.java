package com.qizhi.readflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qizhi.readflow.entity.ConversionHistory;
import com.qizhi.readflow.mapper.ConversionHistoryMapper;
import com.qizhi.readflow.service.ConversionHistoryService;
import org.springframework.stereotype.Service;

/**
 * 转换历史服务实现
 */
@Service
public class ConversionHistoryServiceImpl extends ServiceImpl<ConversionHistoryMapper, ConversionHistory>
        implements ConversionHistoryService {

    @Override
    public Page<ConversionHistory> pageByUserId(Long userId, Integer page, Integer pageSize) {
        Page<ConversionHistory> pageParam = new Page<>(page, pageSize);
        return page(pageParam, new LambdaQueryWrapper<ConversionHistory>()
                .eq(ConversionHistory::getUserId, userId)
                .orderByDesc(ConversionHistory::getCreatedAt));
    }

    @Override
    public ConversionHistory createHistory(Long userId, String title, String type, String text, String voiceId) {
        ConversionHistory history = new ConversionHistory();
        history.setUserId(userId);
        history.setTitle(title);
        history.setType(type);
        history.setOriginalText(text);
        history.setVoiceId(voiceId);
        history.setStatus("pending");
        save(history);
        return history;
    }
}

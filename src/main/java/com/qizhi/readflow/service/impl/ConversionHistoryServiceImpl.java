package com.qizhi.readflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qizhi.readflow.entity.ConversionHistory;
import com.qizhi.readflow.mapper.ConversionHistoryMapper;
import com.qizhi.readflow.service.ConversionHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 转换历史服务实现
 */
@Slf4j
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
        log.info("创建转换记录: id={}, userId={}, title={}", history.getId(), userId, title);
        return history;
    }

    @Override
    public void completeHistory(Long historyId, String audioUrl, Integer audioDuration) {
        ConversionHistory history = getById(historyId);
        if (history != null) {
            history.setAudioUrl(audioUrl);
            history.setAudioDuration(audioDuration);
            history.setStatus("completed");
            history.setUpdatedAt(LocalDateTime.now());
            updateById(history);
            log.info("转换记录完成: id={}, audioUrl={}, duration={}s", historyId, audioUrl, audioDuration);
        }
    }

    @Override
    public void failHistory(Long historyId, String error) {
        ConversionHistory history = getById(historyId);
        if (history != null) {
            history.setStatus("failed");
            history.setUpdatedAt(LocalDateTime.now());
            updateById(history);
            log.error("转换记录失败: id={}, error={}", historyId, error);
        }
    }
}

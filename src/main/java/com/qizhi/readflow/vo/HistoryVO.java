package com.qizhi.readflow.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 转换历史 VO
 */
@Data
public class HistoryVO {

    private String id;
    private String title;
    private String type;
    private LocalDateTime createdAt;
    private String status;
    private Integer audioDuration;
    private String audioUrl;
    private String voiceId;

    public static HistoryVO fromEntity(com.qizhi.readflow.entity.ConversionHistory history) {
        HistoryVO vo = new HistoryVO();
        vo.setId("h_" + history.getId());
        vo.setTitle(history.getTitle());
        vo.setType(history.getType());
        vo.setCreatedAt(history.getCreatedAt());
        vo.setStatus(history.getStatus());
        vo.setAudioDuration(history.getAudioDuration());
        vo.setAudioUrl(history.getAudioUrl());
        vo.setVoiceId(history.getVoiceId());
        return vo;
    }
}

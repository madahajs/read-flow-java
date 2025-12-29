package com.qizhi.readflow.vo;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.List;

/**
 * 语音信息 VO
 */
@Data
public class VoiceVO {

    private String id;
    private String name;
    private String region;
    private String language;
    private String gender;
    private String avatarUrl;
    private List<String> tags;

    public static VoiceVO fromEntity(com.qizhi.readflow.entity.Voice voice) {
        VoiceVO vo = new VoiceVO();
        vo.setId(voice.getId());
        vo.setName(voice.getName());
        vo.setRegion(voice.getRegion());
        vo.setLanguage(voice.getLanguage());
        vo.setGender(voice.getGender());
        vo.setAvatarUrl(voice.getAvatarUrl());
        // 解析 JSON 数组
        if (voice.getTags() != null && !voice.getTags().isEmpty()) {
            vo.setTags(JSONUtil.toList(voice.getTags(), String.class));
        }
        return vo;
    }
}

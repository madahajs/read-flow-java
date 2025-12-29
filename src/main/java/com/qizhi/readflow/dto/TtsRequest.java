package com.qizhi.readflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TTS 生成请求
 */
@Data
public class TtsRequest {

    @NotBlank(message = "文本内容不能为空")
    private String text;

    @NotBlank(message = "语音ID不能为空")
    private String voiceId;

    /**
     * 语速，默认 1.0 (范围 0.5 - 2.0)
     */
    private Double speed = 1.0;

    /**
     * 音频格式，默认 mp3
     */
    private String format = "mp3";
}

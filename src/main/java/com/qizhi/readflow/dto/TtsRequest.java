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
     * 语速，默认 0.9 (范围 0.5 - 2.0)
     * Azure 神经网络语音默认语速较快，0.9 接近正常阅读速度
     */
    private Double speed = 0.9;

    /**
     * 音频格式，默认 mp3
     */
    private String format = "mp3";
}

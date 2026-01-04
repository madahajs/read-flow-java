package com.qizhi.readflow.service.tts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * TTS 合成结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TtsResult {

    /**
     * 音频数据（字节数组）
     */
    private byte[] audioData;

    /**
     * 音频格式（mp3, wav 等）
     */
    private String format;

    /**
     * 音频时长（秒）
     */
    private double duration;

    /**
     * 字幕列表
     */
    private List<Subtitle> subtitles;

    /**
     * 字幕信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subtitle {
        private int id;
        private double startTime;
        private double endTime;
        private String text;
    }
}

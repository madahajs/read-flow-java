package com.qizhi.readflow.service.tts;

import com.qizhi.readflow.dto.TtsRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock TTS 提供商实现
 * 用于测试环境
 */
@Slf4j
public class MockTtsProvider implements TtsProvider {

    @Override
    public TtsResult synthesize(TtsRequest request) throws Exception {
        log.info("Mock TTS 合成: text={}, voiceId={}", request.getText().length(), request.getVoiceId());

        // 模拟生成字幕
        List<TtsResult.Subtitle> subtitles = new ArrayList<>();
        String[] sentences = request.getText().split("[。！？]");
        double currentTime = 0.0;

        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (!sentence.isEmpty()) {
                double duration = sentence.length() * 0.15 / request.getSpeed();
                subtitles.add(TtsResult.Subtitle.builder()
                        .id(i + 1)
                        .startTime(currentTime)
                        .endTime(currentTime + duration)
                        .text(sentence)
                        .build());
                currentTime += duration;
            }
        }

        // 返回空音频数据（Mock 模式）
        return TtsResult.builder()
                .audioData(new byte[0])
                .format(request.getFormat())
                .duration(currentTime)
                .subtitles(subtitles)
                .build();
    }

    @Override
    public String getProviderName() {
        return "Mock";
    }
}

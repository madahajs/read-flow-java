package com.qizhi.readflow.controller;

import com.qizhi.readflow.common.Result;
import com.qizhi.readflow.dto.TtsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * TTS 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tts")
@RequiredArgsConstructor
public class TtsController {

    /**
     * 文本转语音
     */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generate(@Valid @RequestBody TtsRequest request) {
        log.info("TTS generate request: voiceId={}, textLength={}", request.getVoiceId(), request.getText().length());

        // TODO: 实际项目中应调用 TTS 服务（如微软 Azure Speech、阿里云语音合成等）
        // 这里返回模拟数据

        // 模拟生成字幕
        List<Map<String, Object>> subtitles = new ArrayList<>();
        String[] sentences = request.getText().split("[。！？]");
        double currentTime = 0.0;

        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (!sentence.isEmpty()) {
                double duration = sentence.length() * 0.15; // 假设每个字 0.15 秒
                Map<String, Object> subtitle = new HashMap<>();
                subtitle.put("id", i + 1);
                subtitle.put("startTime", currentTime);
                subtitle.put("endTime", currentTime + duration);
                subtitle.put("text", sentence);
                subtitles.add(subtitle);
                currentTime += duration;
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("audioUrl", "https://cdn.example.com/audio/" + UUID.randomUUID() + "." + request.getFormat());
        data.put("duration", currentTime);
        data.put("subtitles", subtitles);

        return Result.success(data);
    }
}

package com.qizhi.readflow.controller;

import com.qizhi.readflow.common.Result;
import com.qizhi.readflow.dto.TtsRequest;
import com.qizhi.readflow.entity.ConversionHistory;
import com.qizhi.readflow.service.ConversionHistoryService;
import com.qizhi.readflow.service.TtsService;
import com.qizhi.readflow.service.tts.TtsResult;
import com.qizhi.readflow.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TTS 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tts")
@RequiredArgsConstructor
public class TtsController {

    private final TtsService ttsService;
    private final ConversionHistoryService historyService;
    private final JwtUtil jwtUtil;

    /**
     * 文本转语音
     */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generate(
            @Valid @RequestBody TtsRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        log.info("TTS generate request: voiceId={}, speed={}, format={}, textLength={}",
                request.getVoiceId(), request.getSpeed(), request.getFormat(), request.getText().length());

        // 从 Token 获取用户 ID
        Long userId = getUserIdFromToken(authorization);

        // 生成标题（取文本前20个字符）
        String title = request.getText().length() > 20
                ? request.getText().substring(0, 20) + "..."
                : request.getText();

        // 创建历史记录（状态：pending）
        ConversionHistory history = null;
        if (userId != null) {
            history = historyService.createHistory(userId, title, "text", request.getText(), request.getVoiceId());
        }

        try {
            // 调用 TTS 服务进行语音合成
            TtsResult result = ttsService.synthesize(request);

            // 构建响应
            Map<String, Object> data = new HashMap<>();

            String audioUrl = null;
            // 如果有音频数据，生成 Base64 或 URL
            if (result.getAudioData() != null && result.getAudioData().length > 0) {
                String base64Audio = Base64.getEncoder().encodeToString(result.getAudioData());
                data.put("audioBase64", base64Audio);
                audioUrl = "data:audio/" + result.getFormat() + ";base64," + base64Audio;
                data.put("audioUrl", audioUrl);
            } else {
                // Mock 模式返回示例 URL
                audioUrl = "https://cdn.example.com/audio/" + UUID.randomUUID() + "." + request.getFormat();
                data.put("audioUrl", audioUrl);
            }

            data.put("duration", result.getDuration());
            data.put("format", result.getFormat());

            // 转换字幕格式
            List<Map<String, Object>> subtitles = result.getSubtitles().stream()
                    .map(sub -> {
                        Map<String, Object> subtitle = new HashMap<>();
                        subtitle.put("id", sub.getId());
                        subtitle.put("startTime", sub.getStartTime());
                        subtitle.put("endTime", sub.getEndTime());
                        subtitle.put("text", sub.getText());
                        return subtitle;
                    })
                    .collect(Collectors.toList());
            data.put("subtitles", subtitles);

            // 更新历史记录为完成状态
            if (history != null) {
                // 对于 Base64 URL，只保存一个标识（实际项目中应该保存文件到OSS然后返回真实URL）
                String savedAudioUrl = audioUrl.startsWith("data:")
                        ? "/audio/" + UUID.randomUUID() + "." + result.getFormat()
                        : audioUrl;
                historyService.completeHistory(history.getId(), savedAudioUrl, (int) result.getDuration());
                data.put("historyId", "h_" + history.getId());
            }

            return Result.success(data);

        } catch (Exception e) {
            // 更新历史记录为失败状态
            if (history != null) {
                historyService.failHistory(history.getId(), e.getMessage());
            }
            throw e;
        }
    }

    /**
     * 从 Authorization Header 中解析用户 ID
     */
    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authorization.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            log.warn("解析 Token 失败: {}", e.getMessage());
            return null;
        }
    }
}

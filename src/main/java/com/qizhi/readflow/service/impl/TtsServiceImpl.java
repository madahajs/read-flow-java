package com.qizhi.readflow.service.impl;

import com.qizhi.readflow.common.BusinessException;
import com.qizhi.readflow.config.TtsConfig;
import com.qizhi.readflow.dto.TtsRequest;
import com.qizhi.readflow.service.TtsService;
import com.qizhi.readflow.service.tts.AzureTtsProvider;
import com.qizhi.readflow.service.tts.MockTtsProvider;
import com.qizhi.readflow.service.tts.TtsProvider;
import com.qizhi.readflow.service.tts.TtsResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * TTS 服务实现类
 * 支持多提供商切换
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TtsServiceImpl implements TtsService {

    private final TtsConfig ttsConfig;
    private TtsProvider ttsProvider;

    @PostConstruct
    public void init() {
        String provider = ttsConfig.getProvider();
        log.info("初始化 TTS 提供商: {}", provider);

        switch (provider.toLowerCase()) {
            case "azure":
                this.ttsProvider = new AzureTtsProvider(ttsConfig.getAzure());
                break;
            case "mock":
                this.ttsProvider = new MockTtsProvider();
                break;
            default:
                log.warn("未知的 TTS 提供商: {}，使用 Mock", provider);
                this.ttsProvider = new MockTtsProvider();
        }

        // 确保音频目录存在
        try {
            Path audioPath = Paths.get(ttsConfig.getAudioPath());
            if (!Files.exists(audioPath)) {
                Files.createDirectories(audioPath);
                log.info("创建音频目录: {}", audioPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("创建音频目录失败", e);
        }

        log.info("TTS 提供商已初始化: {}", ttsProvider.getProviderName());
    }

    @Override
    public TtsResult synthesize(TtsRequest request) {
        try {
            TtsResult result = ttsProvider.synthesize(request);

            // 如果有音频数据，保存到文件
            if (result.getAudioData() != null && result.getAudioData().length > 0) {
                String filename = UUID.randomUUID() + "." + result.getFormat();
                String filepath = saveAudioFile(result.getAudioData(), filename);
                log.info("TTS ({}) 合成成功，音频保存到: {}", ttsProvider.getProviderName(), filepath);
            }

            return result;
        } catch (Exception e) {
            log.error("TTS 合成失败: {}", e.getMessage(), e);
            throw new BusinessException("语音合成失败: " + e.getMessage());
        }
    }

    /**
     * 保存音频文件
     */
    private String saveAudioFile(byte[] audioData, String filename) throws IOException {
        Path audioPath = Paths.get(ttsConfig.getAudioPath(), filename);
        try (FileOutputStream fos = new FileOutputStream(audioPath.toFile())) {
            fos.write(audioData);
        }
        return audioPath.toAbsolutePath().toString();
    }
}

package com.qizhi.readflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * TTS 配置类
 * 支持多提供商配置切换
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tts")
public class TtsConfig {

    /**
     * TTS 提供商选择
     * 可选值: azure, mock
     */
    private String provider = "azure";

    /**
     * Azure AI Speech 配置
     */
    private AzureConfig azure = new AzureConfig();

    /**
     * 音频文件存储路径
     */
    private String audioPath = "./audio";

    @Data
    public static class AzureConfig {
        /**
         * Azure Speech 订阅密钥
         */
        private String subscriptionKey;

        /**
         * Azure Speech 区域
         */
        private String region;
    }
}

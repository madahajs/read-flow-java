package com.qizhi.readflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OCR 配置类
 * 支持多提供商配置切换
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ocr")
public class OcrConfig {

    /**
     * OCR 提供商选择
     * 可选值: azure, tesseract
     */
    private String provider = "azure";

    /**
     * Azure AI Vision 配置
     */
    private AzureConfig azure = new AzureConfig();

    /**
     * Tesseract 配置
     */
    private TesseractConfig tesseract = new TesseractConfig();

    @Data
    public static class AzureConfig {
        /**
         * Azure Cognitive Services 端点
         */
        private String endpoint;

        /**
         * Azure API 密钥
         */
        private String apiKey;
    }

    @Data
    public static class TesseractConfig {
        /**
         * Tesseract 数据文件路径
         */
        private String dataPath;

        /**
         * 识别语言（默认：简体中文+英文）
         */
        private String language = "chi_sim+eng";
    }
}

package com.qizhi.readflow.service.ocr;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.qizhi.readflow.config.OcrConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Azure AI Vision OCR 提供商实现
 */
@Slf4j
public class AzureOcrProvider implements OcrProvider {

    private final OcrConfig.AzureConfig config;
    private ImageAnalysisClient client;

    public AzureOcrProvider(OcrConfig.AzureConfig config) {
        this.config = config;
        initClient();
    }

    private void initClient() {
        if (config.getEndpoint() == null || config.getApiKey() == null) {
            log.warn("Azure OCR 配置不完整，endpoint 或 apiKey 为空");
            return;
        }

        this.client = new ImageAnalysisClientBuilder()
                .endpoint(config.getEndpoint())
                .credential(new AzureKeyCredential(config.getApiKey()))
                .buildClient();

        log.info("Azure AI Vision 客户端初始化成功，endpoint: {}", config.getEndpoint());
    }

    @Override
    public String extractText(MultipartFile file) throws Exception {
        if (client == null) {
            throw new IllegalStateException("Azure OCR 客户端未初始化，请检查配置");
        }

        byte[] imageBytes = file.getBytes();
        return extractTextFromImage(imageBytes);
    }

    @Override
    public String extractTextFromImage(byte[] imageBytes) throws Exception {
        if (client == null) {
            throw new IllegalStateException("Azure OCR 客户端未初始化，请检查配置");
        }

        BinaryData imageData = BinaryData.fromBytes(imageBytes);

        // 使用 READ 功能进行 OCR
        List<VisualFeatures> features = Arrays.asList(VisualFeatures.READ);

        ImageAnalysisResult result = client.analyze(
                imageData,
                features,
                new ImageAnalysisOptions());

        // 提取识别的文字
        StringBuilder textBuilder = new StringBuilder();
        ReadResult readResult = result.getRead();

        if (readResult != null && readResult.getBlocks() != null) {
            for (DetectedTextBlock block : readResult.getBlocks()) {
                for (DetectedTextLine line : block.getLines()) {
                    textBuilder.append(line.getText()).append("\n");
                }
            }
        }

        String extractedText = textBuilder.toString().trim();
        log.debug("Azure OCR 识别完成，文字长度: {}", extractedText.length());

        return extractedText;
    }

    @Override
    public String getProviderName() {
        return "Azure AI Vision";
    }
}

package com.qizhi.readflow.service.impl;

import com.qizhi.readflow.common.BusinessException;
import com.qizhi.readflow.config.OcrConfig;
import com.qizhi.readflow.service.OcrService;
import com.qizhi.readflow.service.ocr.AzureOcrProvider;
import com.qizhi.readflow.service.ocr.OcrProvider;
import com.qizhi.readflow.service.ocr.TesseractOcrProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * OCR 服务实现类
 * 支持多提供商切换
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OcrConfig ocrConfig;
    private OcrProvider ocrProvider;

    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("[a-zA-Z]");

    @PostConstruct
    public void init() {
        String provider = ocrConfig.getProvider();
        log.info("初始化 OCR 提供商: {}", provider);

        switch (provider.toLowerCase()) {
            case "azure":
                this.ocrProvider = new AzureOcrProvider(ocrConfig.getAzure());
                break;
            case "tesseract":
                this.ocrProvider = new TesseractOcrProvider(ocrConfig.getTesseract());
                break;
            default:
                log.warn("未知的 OCR 提供商: {}，使用默认 Azure", provider);
                this.ocrProvider = new AzureOcrProvider(ocrConfig.getAzure());
        }

        log.info("OCR 提供商已初始化: {}", ocrProvider.getProviderName());
    }

    @Override
    public Map<String, Object> extractText(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BusinessException("无法获取文件名");
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        String extractedText;

        try {
            switch (extension) {
                case "jpg":
                case "jpeg":
                case "png":
                    extractedText = ocrProvider.extractText(file);
                    break;
                case "pdf":
                    extractedText = extractFromPdf(file);
                    break;
                case "docx":
                    extractedText = extractFromDocx(file);
                    break;
                default:
                    throw new BusinessException("不支持的文件格式: " + extension);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("OCR 提取文字失败: {}", e.getMessage(), e);
            throw new BusinessException("文字识别失败: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("text", extractedText);
        result.put("detectedLanguage", detectLanguage(extractedText));
        result.put("provider", ocrProvider.getProviderName());

        log.info("OCR ({}) 成功从文件 {} 提取文字，长度: {}",
                ocrProvider.getProviderName(), filename, extractedText.length());
        return result;
    }

    /**
     * 从 PDF 中提取文字
     */
    private String extractFromPdf(MultipartFile file) throws Exception {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("ocr_", "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            try (PDDocument document = Loader.loadPDF(tempFile.toFile())) {
                // 首先尝试直接提取文本
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document).trim();

                // 如果文本太短，使用 OCR
                if (text.length() < 10) {
                    log.info("PDF 直接提取文本较少，尝试使用 OCR");
                    text = extractFromPdfWithOcr(document);
                }

                return text;
            }
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    log.warn("删除临时文件失败: {}", tempFile, e);
                }
            }
        }
    }

    /**
     * 使用 OCR 从 PDF 页面图像中提取文字
     */
    private String extractFromPdfWithOcr(PDDocument document) throws Exception {
        PDFRenderer renderer = new PDFRenderer(document);
        StringBuilder textBuilder = new StringBuilder();

        int pageCount = document.getNumberOfPages();
        for (int i = 0; i < pageCount; i++) {
            BufferedImage image = renderer.renderImageWithDPI(i, 300);

            // 将图像转为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // 使用 OCR 提供商识别
            String pageText = ocrProvider.extractTextFromImage(imageBytes);
            textBuilder.append(pageText.trim());

            if (i < pageCount - 1) {
                textBuilder.append("\n\n");
            }
        }

        return textBuilder.toString();
    }

    /**
     * 从 Word 文档中提取文字
     */
    private String extractFromDocx(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
                XWPFDocument document = new XWPFDocument(is)) {

            StringBuilder textBuilder = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.isEmpty()) {
                    textBuilder.append(text).append("\n");
                }
            }

            return textBuilder.toString().trim();
        }
    }

    /**
     * 检测文本语言
     */
    private String detectLanguage(String text) {
        if (text == null || text.isEmpty()) {
            return "unknown";
        }

        long chineseCount = CHINESE_PATTERN.matcher(text).results().count();
        long englishCount = ENGLISH_PATTERN.matcher(text).results().count();

        if (chineseCount > englishCount) {
            return "zh-CN";
        } else if (englishCount > chineseCount) {
            return "en";
        } else {
            return "mixed";
        }
    }
}

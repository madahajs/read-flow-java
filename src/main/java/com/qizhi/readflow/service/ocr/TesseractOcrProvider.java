package com.qizhi.readflow.service.ocr;

import com.qizhi.readflow.config.OcrConfig;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Tesseract OCR 提供商实现
 */
@Slf4j
public class TesseractOcrProvider implements OcrProvider {

    private final OcrConfig.TesseractConfig config;

    public TesseractOcrProvider(OcrConfig.TesseractConfig config) {
        this.config = config;
    }

    @Override
    public String extractText(MultipartFile file) throws Exception {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("ocr_", "_" + file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            ITesseract tesseract = createTesseract();
            String result = tesseract.doOCR(tempFile.toFile());
            return result.trim();
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

    @Override
    public String extractTextFromImage(byte[] imageBytes) throws Exception {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("ocr_image_", ".png");
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ImageIO.write(image, "png", tempFile.toFile());

            ITesseract tesseract = createTesseract();
            String result = tesseract.doOCR(tempFile.toFile());
            return result.trim();
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

    @Override
    public String getProviderName() {
        return "Tesseract";
    }

    private ITesseract createTesseract() {
        Tesseract tesseract = new Tesseract();

        String dataPath = config.getDataPath();
        String language = config.getLanguage();

        if (dataPath != null && !dataPath.isEmpty()) {
            tesseract.setDatapath(dataPath);
            log.debug("使用配置的 tessdata 路径: {}", dataPath);
        } else {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("mac")) {
                String homebrewPath = "/opt/homebrew/share/tessdata";
                String usrLocalPath = "/usr/local/share/tessdata";
                if (new File(homebrewPath).exists()) {
                    tesseract.setDatapath(homebrewPath);
                } else if (new File(usrLocalPath).exists()) {
                    tesseract.setDatapath(usrLocalPath);
                }
            } else if (osName.contains("linux")) {
                tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
            }
        }

        tesseract.setLanguage(language != null ? language : "chi_sim+eng");
        tesseract.setOcrEngineMode(1);
        tesseract.setPageSegMode(3);

        return tesseract;
    }
}

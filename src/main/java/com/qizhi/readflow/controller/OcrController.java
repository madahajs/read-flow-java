package com.qizhi.readflow.controller;

import com.qizhi.readflow.common.BusinessException;
import com.qizhi.readflow.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OCR 控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ocr")
@RequiredArgsConstructor
public class OcrController {

    private static final List<String> SUPPORTED_FORMATS = Arrays.asList("jpg", "jpeg", "png", "pdf", "docx");

    /**
     * 提取文档文字
     */
    @PostMapping("/extract")
    public Result<Map<String, Object>> extract(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 检查文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("无法获取文件名");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(extension)) {
            throw new BusinessException("Unsupported file format");
        }

        // TODO: 实际项目中应调用 OCR 服务进行文字识别
        // 这里返回模拟数据
        Map<String, Object> data = new HashMap<>();
        data.put("text", "这是从文件 " + originalFilename + " 中识别出的文字内容。\n" +
                "实际项目中，这里应该调用OCR服务（如百度OCR、阿里云OCR或开源的Tesseract）来进行文字识别。");
        data.put("detectedLanguage", "zh-CN");

        log.info("OCR extract file: {}", originalFilename);
        return Result.success(data);
    }
}

package com.qizhi.readflow.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * OCR 服务接口
 */
public interface OcrService {

    /**
     * 从文件中提取文字
     *
     * @param file 上传的文件
     * @return 包含提取文字和检测语言的 Map
     */
    Map<String, Object> extractText(MultipartFile file);
}

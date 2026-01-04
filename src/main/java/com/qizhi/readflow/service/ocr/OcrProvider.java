package com.qizhi.readflow.service.ocr;

import org.springframework.web.multipart.MultipartFile;

/**
 * OCR 提供商接口
 * 定义统一的 OCR 文字提取方法
 */
public interface OcrProvider {

    /**
     * 从文件中提取文字
     *
     * @param file 上传的文件（图片、PDF 等）
     * @return 提取的文字内容
     * @throws Exception 提取失败时抛出异常
     */
    String extractText(MultipartFile file) throws Exception;

    /**
     * 从图片字节数组中提取文字
     *
     * @param imageBytes 图片字节数组
     * @return 提取的文字内容
     * @throws Exception 提取失败时抛出异常
     */
    String extractTextFromImage(byte[] imageBytes) throws Exception;

    /**
     * 获取提供商名称
     *
     * @return 提供商名称
     */
    String getProviderName();
}

package com.qizhi.readflow.service.tts;

import com.qizhi.readflow.dto.TtsRequest;

/**
 * TTS 提供商接口
 * 定义统一的文本转语音方法
 */
public interface TtsProvider {

    /**
     * 将文本转换为语音
     *
     * @param request TTS 请求参数
     * @return TTS 结果（包含音频数据和字幕）
     * @throws Exception 转换失败时抛出异常
     */
    TtsResult synthesize(TtsRequest request) throws Exception;

    /**
     * 获取提供商名称
     *
     * @return 提供商名称
     */
    String getProviderName();
}

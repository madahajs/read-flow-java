package com.qizhi.readflow.service;

import com.qizhi.readflow.dto.TtsRequest;
import com.qizhi.readflow.service.tts.TtsResult;

/**
 * TTS 服务接口
 */
public interface TtsService {

    /**
     * 将文本转换为语音
     *
     * @param request TTS 请求参数
     * @return TTS 结果
     */
    TtsResult synthesize(TtsRequest request);
}

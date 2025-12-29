package com.qizhi.readflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qizhi.readflow.entity.Voice;

import java.util.List;

/**
 * 语音服务接口
 */
public interface VoiceService extends IService<Voice> {

    /**
     * 获取所有可用语音列表
     */
    List<Voice> listAvailable();
}

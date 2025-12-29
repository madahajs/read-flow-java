package com.qizhi.readflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qizhi.readflow.entity.Voice;
import com.qizhi.readflow.mapper.VoiceMapper;
import com.qizhi.readflow.service.VoiceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 语音服务实现
 */
@Service
public class VoiceServiceImpl extends ServiceImpl<VoiceMapper, Voice> implements VoiceService {

    @Override
    public List<Voice> listAvailable() {
        return list(new LambdaQueryWrapper<Voice>()
                .eq(Voice::getStatus, 1)
                .orderByAsc(Voice::getSortOrder));
    }
}

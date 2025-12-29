package com.qizhi.readflow.controller;

import com.qizhi.readflow.common.Result;
import com.qizhi.readflow.service.VoiceService;
import com.qizhi.readflow.vo.VoiceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 语音控制器
 */
@RestController
@RequestMapping("/api/v1/voices")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;

    /**
     * 获取可用语音列表
     */
    @GetMapping
    public Result<List<VoiceVO>> list() {
        List<VoiceVO> voices = voiceService.listAvailable().stream()
                .map(VoiceVO::fromEntity)
                .collect(Collectors.toList());
        return Result.success(voices);
    }
}

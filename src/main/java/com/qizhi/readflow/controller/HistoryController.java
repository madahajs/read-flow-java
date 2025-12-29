package com.qizhi.readflow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qizhi.readflow.common.BusinessException;
import com.qizhi.readflow.common.PageResult;
import com.qizhi.readflow.common.Result;
import com.qizhi.readflow.entity.ConversionHistory;
import com.qizhi.readflow.service.ConversionHistoryService;
import com.qizhi.readflow.vo.HistoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 历史记录控制器
 */
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final ConversionHistoryService historyService;

    /**
     * 获取转换历史（分页）
     * TODO: 实际项目中应从 token 中解析用户ID
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        // 模拟用户ID为2 (实际应从 token 中获取)
        Long userId = 2L;

        Page<ConversionHistory> pageResult = historyService.pageByUserId(userId, page, pageSize);
        List<HistoryVO> list = pageResult.getRecords().stream()
                .map(HistoryVO::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", pageResult.getTotal());

        return Result.success(data);
    }

    /**
     * 删除历史记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        // 解析 ID (格式: h_123)
        if (!id.startsWith("h_")) {
            throw new BusinessException("无效的记录ID");
        }

        Long historyId = Long.parseLong(id.substring(2));
        boolean removed = historyService.removeById(historyId);

        if (!removed) {
            throw new BusinessException("记录不存在或已被删除");
        }

        return Result.success();
    }
}

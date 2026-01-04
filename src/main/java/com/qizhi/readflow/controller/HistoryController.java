package com.qizhi.readflow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qizhi.readflow.common.BusinessException;
import com.qizhi.readflow.common.Result;
import com.qizhi.readflow.entity.ConversionHistory;
import com.qizhi.readflow.service.ConversionHistoryService;
import com.qizhi.readflow.util.JwtUtil;
import com.qizhi.readflow.vo.HistoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 历史记录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final ConversionHistoryService historyService;
    private final JwtUtil jwtUtil;

    /**
     * 获取转换历史（分页）
     */
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        // 从 Token 获取用户 ID
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        log.info("查询转换历史: userId={}, page={}, pageSize={}", userId, page, pageSize);

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

        // 从 Token 获取用户 ID
        Long userId = getUserIdFromToken(authorization);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 解析 ID (格式: h_123)
        if (!id.startsWith("h_")) {
            throw new BusinessException("无效的记录ID");
        }

        Long historyId = Long.parseLong(id.substring(2));

        // 验证记录属于当前用户
        ConversionHistory history = historyService.getById(historyId);
        if (history == null) {
            throw new BusinessException("记录不存在");
        }
        if (!history.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此记录");
        }

        boolean removed = historyService.removeById(historyId);
        if (!removed) {
            throw new BusinessException("删除失败");
        }

        log.info("删除转换记录: id={}, userId={}", historyId, userId);
        return Result.success();
    }

    /**
     * 从 Authorization Header 中解析用户 ID
     */
    private Long getUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authorization.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            log.warn("解析 Token 失败: {}", e.getMessage());
            return null;
        }
    }
}

package com.aheadshop.admin.controller;

import com.aheadshop.admin.domain.vo.UserPageVO;
import com.aheadshop.admin.feign.UserFeignClient;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "管理端-用户管理")
public class AdminUserController {

    private final UserFeignClient userFeignClient;

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    public Result<PageResult<UserPageVO>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return userFeignClient.listUsers(keyword, pageNum, pageSize);
    }

    @Operation(summary = "启用/禁用用户")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam("userId") Long userId,
                                     @RequestParam("status") Integer status) {
        return userFeignClient.updateStatus(userId, status);
    }
}

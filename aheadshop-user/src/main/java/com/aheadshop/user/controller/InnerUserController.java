package com.aheadshop.user.controller;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.user.domain.po.User;
import com.aheadshop.user.domain.vo.AddressVO;
import com.aheadshop.user.domain.vo.UserPageVO;
import com.aheadshop.user.service.IAddressService;
import com.aheadshop.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner/user")
@RequiredArgsConstructor
@Tag(name = "用户内部接口（供其他微服务调用）")
public class InnerUserController {

    private final IAddressService addressService;
    private final IUserService userService;

    @Operation(summary = "获取收货地址详情")
    @GetMapping("/address/{id}")
    public Result<AddressVO> getAddress(@PathVariable("id") Long id) {
        return Result.success(addressService.getByIdForInner(id));
    }

    @Operation(summary = "管理端-用户列表")
    @GetMapping("/list")
    public Result<PageResult<UserPageVO>> listUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return Result.success(userService.adminListUsers(keyword, pageNum, pageSize));
    }

    @Operation(summary = "管理端-启用/禁用用户")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam("userId") Long userId,
                                     @RequestParam("status") Integer status) {
        userService.updateUserStatus(userId, status);
        return Result.success(null);
    }

    @Operation(summary = "今日新增用户数")
    @GetMapping("/today-count")
    public Result<Long> todayNewUserCount() {
        return Result.success(userService.todayNewUserCount());
    }

    @Operation(summary = "根据ID获取用户")
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "通过邀请码查找用户")
    @GetMapping("/by-invite-code/{code}")
    public Result<User> getByInviteCode(@PathVariable("code") String code) {
        return Result.success(userService.getByInviteCode(code));
    }
}

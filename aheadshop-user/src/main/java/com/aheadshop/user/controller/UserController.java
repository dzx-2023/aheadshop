package com.aheadshop.user.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.user.domain.dto.LoginDTO;
import com.aheadshop.user.domain.dto.RegisterDTO;
import com.aheadshop.user.domain.vo.LoginVO;
import com.aheadshop.user.domain.vo.UserInfoVO;
import com.aheadshop.user.service.IUserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理接口")
public class UserController {

    private final IUserService userService;

    @Operation(summary = "用户注册", description = "创建新用户账号")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success(null);
    }

    @Operation(summary = "用户登录", description = "用户名密码登录，返回 token")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @Operation(summary = "获取用户信息", description = "需要携带 token 请求")
    @GetMapping("/info")
    public Result<UserInfoVO> info(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(userService.getUserInfo(userId));
    }
}
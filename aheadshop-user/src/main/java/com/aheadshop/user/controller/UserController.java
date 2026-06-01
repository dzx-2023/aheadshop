package com.aheadshop.user.controller;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.user.domain.dto.AddressDTO;
import com.aheadshop.user.domain.dto.LoginDTO;
import com.aheadshop.user.domain.dto.RegisterDTO;
import com.aheadshop.user.domain.dto.UpdateUserDTO;
import com.aheadshop.user.domain.vo.AddressVO;
import com.aheadshop.user.domain.vo.LoginVO;
import com.aheadshop.user.domain.vo.UserInfoVO;
import com.aheadshop.user.service.AuthService;
import com.aheadshop.user.service.IAddressService;
import com.aheadshop.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理接口")
public class UserController {

    private final IUserService userService;
    private final IAddressService addressService;
    private final AuthService authService;

    // ========== 认证 ==========

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

    @Operation(summary = "刷新 Token", description = "用 refreshToken 换新的 accessToken")
    @PostMapping("/refresh-token")
    public Result<Map<String, String>> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        String[] tokens = authService.refreshToken(refreshToken);
        return Result.success(Map.of("accessToken", tokens[0], "refreshToken", tokens[1]));
    }

    // ========== 用户信息 ==========

    @Operation(summary = "获取用户信息", description = "需要携带 token 请求")
    @GetMapping("/info")
    public Result<UserInfoVO> info(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "修改用户信息", description = "修改昵称/头像/性别")
    @PutMapping("/info")
    public Result<Void> updateInfo(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody UpdateUserDTO dto) {
        userService.updateUserInfo(userId, dto);
        return Result.success(null);
    }

    @Operation(summary = "上传头像", description = "上传头像图片，返回图片 URL")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestHeader("X-User-Id") Long userId,
                                       @RequestParam("file") MultipartFile file) {
        String url = userService.uploadAvatar(userId, file);
        return Result.success(url);
    }

    // ========== 收货地址 ==========

    @Operation(summary = "收货地址列表")
    @GetMapping("/address/list")
    public Result<List<AddressVO>> addressList(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(addressService.listByUserId(userId));
    }

    @Operation(summary = "新增收货地址")
    @PostMapping("/address")
    public Result<Void> addAddress(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody AddressDTO dto) {
        addressService.addAddress(userId, dto);
        return Result.success(null);
    }

    @Operation(summary = "修改收货地址")
    @PutMapping("/address")
    public Result<Void> updateAddress(@RequestHeader("X-User-Id") Long userId,
                                      @Valid @RequestBody AddressDTO dto) {
        addressService.updateAddress(userId, dto);
        return Result.success(null);
    }

    @Operation(summary = "删除收货地址")
    @DeleteMapping("/address/{id}")
    public Result<Void> deleteAddress(@RequestHeader("X-User-Id") Long userId,
                                      @PathVariable Long id) {
        addressService.deleteAddress(userId, id);
        return Result.success(null);
    }

    @Operation(summary = "设为默认地址")
    @PutMapping("/address/default/{id}")
    public Result<Void> setDefaultAddress(@RequestHeader("X-User-Id") Long userId,
                                          @PathVariable Long id) {
        addressService.setDefault(userId, id);
        return Result.success(null);
    }
}
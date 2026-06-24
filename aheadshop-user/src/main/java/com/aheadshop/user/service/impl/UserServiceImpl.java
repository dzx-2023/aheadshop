package com.aheadshop.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.user.domain.dto.LoginDTO;
import com.aheadshop.user.domain.dto.RegisterDTO;
import com.aheadshop.user.domain.dto.UpdateUserDTO;
import com.aheadshop.user.domain.po.Role;
import com.aheadshop.user.domain.po.User;
import com.aheadshop.user.domain.po.UserRole;
import com.aheadshop.user.domain.vo.LoginVO;
import com.aheadshop.user.domain.vo.UserInfoVO;
import com.aheadshop.user.mapper.RoleMapper;
import com.aheadshop.user.mapper.UserMapper;
import com.aheadshop.user.mapper.UserRoleMapper;
import com.aheadshop.user.service.AuthService;
import com.aheadshop.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.user.domain.vo.UserPageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final AuthService authService;
    private final com.aheadshop.user.feign.DistributionFeignClient distributionFeignClient;

    @Value("${upload.avatar-path:D:/aheadshop/upload/avatar/}")
    private String avatarPath;

    @Value("${upload.avatar-url-prefix:http://localhost:8081/avatar/}")
    private String avatarUrlPrefix;

    private static final Long DEFAULT_ROLE_ID = 2L;
    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024;

    private static final String INVITE_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITE_CODE_LENGTH = 6;

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(BusinessExceptionCode.USER_EXISTS, "用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setGender(0);
        user.setStatus(1);

        // 自动生成 6 位邀请码
        String inviteCode = generateUniqueInviteCode();
        user.setInviteCode(inviteCode);

        this.save(user);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(DEFAULT_ROLE_ID);
        userRoleMapper.insert(userRole);

        // 如果填写了邀请码，绑定推荐关系
        if (dto.getInviteCode() != null && !dto.getInviteCode().isBlank()) {
            try {
                Long referrerUserId = distributionFeignClient.bindReferral(user.getId(), dto.getInviteCode()).getData();
                if (referrerUserId != null) {
                    user.setReferrerId(referrerUserId);
                    this.updateById(user);
                }
            } catch (Exception e) {
                log.warn("绑定推荐关系失败，用户注册不影响: {}", e.getMessage());
            }
        }
    }

    /**
     * 生成唯一的 6 位邀请码（大写字母+数字，排除易混淆字符）
     */
    private String generateUniqueInviteCode() {
        String code;
        int maxRetries = 20;
        do {
            StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
            for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
                sb.append(INVITE_CODE_CHARS.charAt(RandomUtil.randomInt(INVITE_CODE_CHARS.length())));
            }
            code = sb.toString();
            maxRetries--;
        } while (this.count(new LambdaQueryWrapper<User>().eq(User::getInviteCode, code)) > 0 && maxRetries > 0);

        if (maxRetries <= 0) {
            // 极端情况：用 UUID 截取兜底
            code = IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
        }
        return code;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException(BusinessExceptionCode.USER_NOT_FOUND, "用户不存在");
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(BusinessExceptionCode.PASSWORD_ERROR, "密码错误");
        }

        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        String roleKey = roles.isEmpty() ? "USER" : roles.get(0).getRoleKey();

        String[] tokens = authService.generateTokenPair(user.getId(), roleKey);

        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .build();
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(BusinessExceptionCode.USER_NOT_FOUND, "用户不存在");
        }

        List<Role> roles = roleMapper.selectRolesByUserId(userId);
        List<String> roleKeys = roles.stream().map(Role::getRoleKey).collect(Collectors.toList());

        return UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .roles(roleKeys)
                .inviteCode(user.getInviteCode())
                .referrerId(user.getReferrerId())
                .build();
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UpdateUserDTO dto) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(BusinessExceptionCode.USER_NOT_FOUND, "用户不存在");
        }
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        this.updateById(user);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "上传文件不能为空");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "头像大小不能超过 2MB");
        }
        String originalName = file.getOriginalFilename();
        String suffix = originalName != null ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
        String fileName = IdUtil.fastSimpleUUID() + suffix;

        File dest = new File(avatarPath + fileName);
        dest.getParentFile().mkdirs();
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            throw new BusinessException(BusinessExceptionCode.SERVER_ERROR, "头像上传失败");
        }
        String avatarUrl = avatarUrlPrefix + fileName;

        // 将头像 URL 写入数据库
        User user = this.getById(userId);
        if (user != null) {
            user.setAvatar(avatarUrl);
            this.updateById(user);
        }

        return avatarUrl;
    }

    // ========== 管理端接口 ==========

    @Override
    public PageResult<UserPageVO> adminListUsers(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .and(keyword != null && !keyword.isEmpty(), w ->
                        w.like(User::getUsername, keyword)
                                .or().like(User::getNickname, keyword)
                                .or().like(User::getPhone, keyword))
                .orderByDesc(User::getCreateTime);

        Page<User> page = this.page(new Page<>(pageNum, pageSize), wrapper);

        List<UserPageVO> records = page.getRecords().stream()
                .map(user -> UserPageVO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .phone(user.getPhone())
                        .avatar(user.getAvatar())
                        .gender(user.getGender())
                        .status(user.getStatus())
                        .createTime(user.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), records);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(BusinessExceptionCode.USER_NOT_FOUND, "用户不存在");
        }
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public Long todayNewUserCount() {
        LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        return this.count(new LambdaQueryWrapper<User>().ge(User::getCreateTime, startOfDay));
    }

    @Override
    public User getByInviteCode(String inviteCode) {
        return this.getOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, inviteCode));
    }
}
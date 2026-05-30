package com.aheadshop.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.user.domain.dto.LoginDTO;
import com.aheadshop.user.domain.dto.RegisterDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final AuthService authService;

    private static final Long DEFAULT_ROLE_ID = 2L;

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
        this.save(user);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(DEFAULT_ROLE_ID);
        userRoleMapper.insert(userRole);
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
                .build();
    }
}
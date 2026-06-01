package com.aheadshop.user.service;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.user.domain.dto.LoginDTO;
import com.aheadshop.user.domain.dto.RegisterDTO;
import com.aheadshop.user.domain.dto.UpdateUserDTO;
import com.aheadshop.user.domain.vo.LoginVO;
import com.aheadshop.user.domain.vo.UserInfoVO;
import com.aheadshop.user.domain.vo.UserPageVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aheadshop.user.domain.po.User;
import org.springframework.web.multipart.MultipartFile;


public interface IUserService extends IService<User> {

    void register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    UserInfoVO getUserInfo(Long userId);

    void updateUserInfo(Long userId, UpdateUserDTO dto);

    String uploadAvatar(Long userId, MultipartFile file);

    // 管理端接口
    PageResult<UserPageVO> adminListUsers(String keyword, Integer pageNum, Integer pageSize);

    void updateUserStatus(Long userId, Integer status);

    Long todayNewUserCount();
}
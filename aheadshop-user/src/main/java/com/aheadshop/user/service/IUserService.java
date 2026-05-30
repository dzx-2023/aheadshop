package com.aheadshop.user.service;

import com.aheadshop.user.domain.dto.LoginDTO;
import com.aheadshop.user.domain.dto.RegisterDTO;
import com.aheadshop.user.domain.vo.LoginVO;
import com.aheadshop.user.domain.vo.UserInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aheadshop.user.domain.po.User;


public interface IUserService extends IService<User> {

    void register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    UserInfoVO getUserInfo(Long userId);
}
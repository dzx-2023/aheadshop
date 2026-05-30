package com.aheadshop.user.mapper;

import com.aheadshop.user.domain.po.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectRolesByUserId(@Param("userId") Long userId);
}
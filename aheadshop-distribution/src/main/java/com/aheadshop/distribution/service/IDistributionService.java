package com.aheadshop.distribution.service;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.distribution.domain.po.Distributor;
import com.aheadshop.distribution.domain.vo.DistributionInfoVO;
import com.aheadshop.distribution.domain.vo.ReferrerInfoVO;
import com.aheadshop.distribution.domain.vo.TeamMemberVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IDistributionService extends IService<Distributor> {

    /**
     * 为用户生成邀请码并创建分销商记录
     */
    String generateInviteCode(Long userId);

    /**
     * 绑定推荐关系（通过邀请码自动查找推荐人），返回推荐人 userId
     */
    Long bindReferralByInviteCode(Long userId, String inviteCode);

    /**
     * 根据 userId 查询分销商
     */
    Distributor getByUserId(Long userId);

    /**
     * 判断用户是否已是分销商
     */
    boolean existsByUserId(Long userId);

    /**
     * 获取分销中心信息
     */
    DistributionInfoVO getInfo(Long userId);

    /**
     * 获取团队成员列表
     */
    List<TeamMemberVO> getTeamList(Long userId);

    /**
     * 获取当前用户的推荐人信息
     */
    ReferrerInfoVO getReferrerInfo(Long userId);

    /**
     * 解除推荐关系
     */
    void unbindReferral(Long userId);

    /**
     * 管理端-分销商列表
     */
    PageResult<Distributor> adminList(Integer status, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 管理端-启用/禁用分销商
     */
    void adminUpdateStatus(Long userId, Integer status);
}

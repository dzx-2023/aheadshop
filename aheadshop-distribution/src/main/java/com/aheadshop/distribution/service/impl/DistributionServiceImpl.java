package com.aheadshop.distribution.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.distribution.domain.po.CommissionRecord;
import com.aheadshop.distribution.domain.po.Distributor;
import com.aheadshop.distribution.domain.po.ReferralRelation;
import com.aheadshop.distribution.domain.vo.DistributionInfoVO;
import com.aheadshop.distribution.domain.vo.ReferrerInfoVO;
import com.aheadshop.distribution.domain.vo.TeamMemberVO;
import com.aheadshop.distribution.feign.UserFeignClient;
import com.aheadshop.distribution.mapper.CommissionRecordMapper;
import com.aheadshop.distribution.mapper.DistributorMapper;
import com.aheadshop.distribution.mapper.ReferralRelationMapper;
import com.aheadshop.distribution.service.IDistributionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributionServiceImpl extends ServiceImpl<DistributorMapper, Distributor>
        implements IDistributionService {

    private final ReferralRelationMapper referralRelationMapper;
    private final CommissionRecordMapper commissionRecordMapper;
    private final UserFeignClient userFeignClient;

    private static final int INVITE_CODE_LENGTH = 8;

    @Override
    @Transactional
    public String generateInviteCode(Long userId) {
        // 检查是否已存在
        Distributor existing = getByUserId(userId);
        if (existing != null) {
            return existing.getInviteCode();
        }

        String code;
        do {
            code = doGenerate();
        } while (existsByInviteCode(code));

        Distributor d = new Distributor();
        d.setUserId(userId);
        d.setInviteCode(code);
        d.setLevel(1);
        d.setTotalCommission(BigDecimal.ZERO);
        d.setAvailableCommission(BigDecimal.ZERO);
        d.setFrozenCommission(BigDecimal.ZERO);
        d.setTotalTeamCount(0);
        d.setStatus(1);
        this.save(d);

        log.info("为用户 {} 生成邀请码 {}", userId, code);
        return code;
    }

    @Override
    @Transactional
    public Long bindReferralByInviteCode(Long userId, String inviteCode) {
        // 1. 通过邀请码从 user 表查找推荐人
        var referrerRes = userFeignClient.getByInviteCode(inviteCode);
        if (referrerRes == null || referrerRes.getData() == null) {
            throw new BusinessException(BusinessExceptionCode.INVITE_CODE_INVALID, "邀请码无效");
        }
        Long referrerUserId = ((Number) referrerRes.getData().get("id")).longValue();

        // 2. 不能自邀请
        if (userId.equals(referrerUserId)) {
            throw new BusinessException(BusinessExceptionCode.SELF_INVITE_NOT_ALLOWED, "不能使用自己的邀请码");
        }

        // 3. 校验该用户是否已绑定
        long count = referralRelationMapper.selectCount(
                new LambdaQueryWrapper<ReferralRelation>().eq(ReferralRelation::getUserId, userId));
        if (count > 0) {
            throw new BusinessException(BusinessExceptionCode.ALREADY_BINDDED, "该用户已绑定推荐关系");
        }

        // 4. 写入 referral_relation
        ReferralRelation relation = new ReferralRelation();
        relation.setUserId(userId);
        relation.setReferrerId(referrerUserId);
        relation.setInviteCode(inviteCode);
        relation.setCreateTime(LocalDateTime.now());
        referralRelationMapper.insert(relation);

        // 5. 推荐人分销商记录：不存在则自动创建，已存在则 team_count + 1
        Distributor referrer = getByUserId(referrerUserId);
        if (referrer == null) {
            referrer = new Distributor();
            referrer.setUserId(referrerUserId);
            referrer.setInviteCode(inviteCode);
            referrer.setLevel(1);
            referrer.setTotalCommission(BigDecimal.ZERO);
            referrer.setAvailableCommission(BigDecimal.ZERO);
            referrer.setFrozenCommission(BigDecimal.ZERO);
            referrer.setTotalTeamCount(1);
            referrer.setStatus(1);
            this.save(referrer);
            log.info("为推荐人 {} 自动创建分销商记录", referrerUserId);
        } else {
            referrer.setTotalTeamCount(referrer.getTotalTeamCount() + 1);
            this.updateById(referrer);
        }

        log.info("用户 {} 通过邀请码 {} 绑定推荐人 {}", userId, inviteCode, referrerUserId);
        return referrerUserId;
    }

    @Override
    public Distributor getByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return this.count(new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId)) > 0;
    }

    @Override
    public DistributionInfoVO getInfo(Long userId) {
        Distributor d = getByUserId(userId);

        DistributionInfoVO vo = new DistributionInfoVO();
        vo.setUserId(userId);
        if (d == null) {
            // 尚无分销商记录，返回默认值
            vo.setInviteCode("");
            vo.setLevel(1);
            vo.setTotalCommission(BigDecimal.ZERO);
            vo.setAvailableCommission(BigDecimal.ZERO);
            vo.setFrozenCommission(BigDecimal.ZERO);
            vo.setTotalTeamCount(0);
            vo.setStatus(1);
        } else {
            vo.setInviteCode(d.getInviteCode());
            vo.setLevel(d.getLevel());
            vo.setTotalCommission(d.getTotalCommission());
            vo.setAvailableCommission(d.getAvailableCommission());
            vo.setFrozenCommission(d.getFrozenCommission());
            vo.setTotalTeamCount(d.getTotalTeamCount());
            vo.setStatus(d.getStatus());
        }
        return vo;
    }

    @Override
    public List<TeamMemberVO> getTeamList(Long userId) {
        // 没有分销商记录也返回空列表
        if (!existsByUserId(userId)) {
            return new ArrayList<>();
        }

        List<ReferralRelation> relations = referralRelationMapper.selectList(
                new LambdaQueryWrapper<ReferralRelation>()
                        .eq(ReferralRelation::getReferrerId, userId)
                        .orderByDesc(ReferralRelation::getCreateTime));

        // TODO: 通过 UserFeignClient 获取用户昵称和头像，暂时返回 userId
        List<TeamMemberVO> list = new ArrayList<>();
        for (ReferralRelation r : relations) {
            TeamMemberVO vo = new TeamMemberVO();
            vo.setUserId(r.getUserId());
            vo.setNickname("用户" + r.getUserId());
            vo.setAvatar(null);
            vo.setJoinTime(r.getCreateTime());
            list.add(vo);
        }
        return list;
    }

    @Override
    public ReferrerInfoVO getReferrerInfo(Long userId) {
        ReferralRelation relation = referralRelationMapper.selectOne(
                new LambdaQueryWrapper<ReferralRelation>().eq(ReferralRelation::getUserId, userId));
        if (relation == null) {
            return null;
        }

        ReferrerInfoVO vo = new ReferrerInfoVO();
        vo.setUserId(relation.getReferrerId());
        vo.setInviteCode(relation.getInviteCode());
        vo.setBindTime(relation.getCreateTime());

        // 通过 Feign 获取推荐人昵称和头像
        try {
            var res = userFeignClient.getUserById(relation.getReferrerId());
            if (res != null && res.getData() != null) {
                @SuppressWarnings("unchecked")
                var userMap = (java.util.Map<String, Object>) res.getData();
                vo.setNickname((String) userMap.get("nickname"));
                vo.setAvatar((String) userMap.get("avatar"));
            }
        } catch (Exception e) {
            log.warn("获取推荐人用户信息失败: {}", e.getMessage());
            vo.setNickname("用户" + relation.getReferrerId());
        }

        if (vo.getNickname() == null) {
            vo.setNickname("用户" + relation.getReferrerId());
        }

        return vo;
    }

    @Override
    @Transactional
    public void unbindReferral(Long userId) {
        ReferralRelation relation = referralRelationMapper.selectOne(
                new LambdaQueryWrapper<ReferralRelation>().eq(ReferralRelation::getUserId, userId));
        if (relation == null) {
            throw new BusinessException(BusinessExceptionCode.NO_REFERRER_FOUND, "您尚未绑定推荐关系");
        }

        // 检查是否有已结算的佣金，有则不允许解绑
        long settledCount = commissionRecordMapper.selectCount(
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getBuyerId, userId)
                        .in(CommissionRecord::getStatus, 0, 1)); // FROZEN 或 SETTLED
        if (settledCount > 0) {
            throw new BusinessException(BusinessExceptionCode.UNBIND_NOT_ALLOWED, "存在关联佣金记录，无法解绑");
        }

        // 推荐人 total_team_count - 1
        Distributor referrer = getByUserId(relation.getReferrerId());
        if (referrer != null && referrer.getTotalTeamCount() > 0) {
            referrer.setTotalTeamCount(referrer.getTotalTeamCount() - 1);
            this.updateById(referrer);
        }

        // 删除推荐关系
        referralRelationMapper.deleteById(relation.getId());

        log.info("用户 {} 解除了与推荐人 {} 的推荐关系", userId, relation.getReferrerId());
    }

    // ========== 私有方法 ==========

    /**
     * 生成随机邀请码：大写字母 + 数字，长度 8
     */
    private String doGenerate() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            sb.append(chars.charAt(RandomUtil.randomInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 检查邀请码是否已存在
     */
    private boolean existsByInviteCode(String code) {
        return this.count(new LambdaQueryWrapper<Distributor>().eq(Distributor::getInviteCode, code)) > 0;
    }

    @Override
    public PageResult<Distributor> adminList(Integer status, String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Distributor> wrapper = new LambdaQueryWrapper<Distributor>()
                .eq(status != null, Distributor::getStatus, status)
                .and(keyword != null && !keyword.isEmpty(), w ->
                        w.like(Distributor::getInviteCode, keyword)
                                .or().eq(Distributor::getUserId, keyword))
                .orderByDesc(Distributor::getCreateTime);
        Page<Distributor> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }

    @Override
    public void adminUpdateStatus(Long userId, Integer status) {
        Distributor d = getByUserId(userId);
        if (d == null) {
            throw new BusinessException(BusinessExceptionCode.DISTRIBUTOR_NOT_FOUND, "分销商不存在");
        }
        d.setStatus(status);
        this.updateById(d);
        log.info("分销商 {} 状态更新为 {}", userId, status);
    }
}

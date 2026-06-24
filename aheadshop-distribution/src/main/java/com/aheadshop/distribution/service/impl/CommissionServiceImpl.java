package com.aheadshop.distribution.service.impl;

import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.distribution.domain.po.CommissionRecord;
import com.aheadshop.distribution.domain.po.Distributor;
import com.aheadshop.distribution.domain.po.ReferralRelation;
import com.aheadshop.distribution.domain.vo.CommissionSummaryVO;
import com.aheadshop.distribution.enums.CommissionStatus;
import com.aheadshop.distribution.mapper.CommissionRecordMapper;
import com.aheadshop.distribution.mapper.DistributorMapper;
import com.aheadshop.distribution.mapper.ReferralRelationMapper;
import com.aheadshop.distribution.service.ICommissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements ICommissionService {

    private final CommissionRecordMapper commissionRecordMapper;
    private final DistributorMapper distributorMapper;
    private final ReferralRelationMapper referralRelationMapper;

    @Value("${distribution.commission.default-rate:0.1000}")
    private BigDecimal defaultRate;

    @Value("${distribution.commission.freeze-days:7}")
    private int freezeDays;

    @Override
    @Transactional
    public void calculateCommission(String orderNo, Long buyerId, BigDecimal payAmount) {
        // 1. 幽等检查（uk_order_no）
        Long existing = commissionRecordMapper.selectCount(
                new LambdaQueryWrapper<CommissionRecord>().eq(CommissionRecord::getOrderNo, orderNo));
        if (existing > 0) {
            log.info("订单 {} 佣金已计算，跳过", orderNo);
            return;
        }

        // 2. 查买家的 referral_relation → referrer_id
        ReferralRelation relation = referralRelationMapper.selectOne(
                new LambdaQueryWrapper<ReferralRelation>().eq(ReferralRelation::getUserId, buyerId));
        if (relation == null) {
            log.info("买家 {} 无推荐人，不产生佣金", buyerId);
            return;
        }
        Long referrerId = relation.getReferrerId();

        // 3. 查 referrer 的 distributor，不存在则自动创建（人人可赚佣金）
        Distributor referrer = distributorMapper.selectOne(
                new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, referrerId));
        if (referrer == null) {
            referrer = new Distributor();
            referrer.setUserId(referrerId);
            referrer.setInviteCode(""); // 邀请码在 user 表，此处占位
            referrer.setLevel(1);
            referrer.setTotalCommission(BigDecimal.ZERO);
            referrer.setAvailableCommission(BigDecimal.ZERO);
            referrer.setFrozenCommission(BigDecimal.ZERO);
            referrer.setTotalTeamCount(0);
            referrer.setStatus(1);
            distributorMapper.insert(referrer);
            log.info("为推荐人 {} 自动创建分销商记录", referrerId);
        }
        if (referrer.getStatus() != 1) {
            log.warn("推荐人 {} 分销商状态异常（{}），跳过佣金计算", referrerId, referrer.getStatus());
            return;
        }

        // 4. 校验 buyerId ≠ referrerId
        if (buyerId.equals(referrerId)) {
            log.warn("买家 {} 与推荐人相同，跳过佣金计算", buyerId);
            return;
        }

        // 5. 计算佣金 = payAmount × defaultRate (RoundingMode.FLOOR)
        BigDecimal commissionAmount = payAmount.multiply(defaultRate).setScale(2, RoundingMode.FLOOR);
        if (commissionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("佣金金额为 0，跳过");
            return;
        }

        // 6. 写入 commission_record（status=FROZEN）
        CommissionRecord record = new CommissionRecord();
        record.setDistributorId(referrerId);
        record.setOrderNo(orderNo);
        record.setBuyerId(buyerId);
        record.setOrderAmount(payAmount);
        record.setCommissionRate(defaultRate);
        record.setCommissionAmount(commissionAmount);
        record.setStatus(CommissionStatus.FROZEN);
        commissionRecordMapper.insert(record);

        // 7. 更新 distributor: frozen_commission += amount, total_commission += amount
        referrer.setFrozenCommission(referrer.getFrozenCommission().add(commissionAmount));
        referrer.setTotalCommission(referrer.getTotalCommission().add(commissionAmount));
        distributorMapper.updateById(referrer);

        log.info("订单 {} 佣金计算完成: 推荐人={}, 金额={}", orderNo, referrerId, commissionAmount);
    }

    @Override
    @Transactional
    public void cancelCommission(String orderNo) {
        CommissionRecord record = commissionRecordMapper.selectOne(
                new LambdaQueryWrapper<CommissionRecord>().eq(CommissionRecord::getOrderNo, orderNo));
        if (record == null) {
            log.info("订单 {} 无佣金记录，跳过取消", orderNo);
            return;
        }
        if (!Integer.valueOf(CommissionStatus.FROZEN).equals(record.getStatus())) {
            log.warn("订单 {} 佣金状态非冻结（{}），跳过取消", orderNo, record.getStatus());
            return;
        }

        // 更新佣金记录状态
        record.setStatus(CommissionStatus.CANCELLED);
        commissionRecordMapper.updateById(record);

        // 回滚分销商冻结佣金
        Distributor referrer = distributorMapper.selectOne(
                new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, record.getDistributorId()));
        if (referrer != null) {
            referrer.setFrozenCommission(referrer.getFrozenCommission().subtract(record.getCommissionAmount()));
            referrer.setTotalCommission(referrer.getTotalCommission().subtract(record.getCommissionAmount()));
            distributorMapper.updateById(referrer);
        }

        log.info("订单 {} 佣金已取消，分销商 {} 冻结佣金回滚 {}", orderNo, record.getDistributorId(), record.getCommissionAmount());
    }

    @Override
    public CommissionSummaryVO getSummary(Long userId) {
        Distributor d = distributorMapper.selectOne(
                new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));

        CommissionSummaryVO vo = new CommissionSummaryVO();
        if (d == null) {
            vo.setTotalCommission(BigDecimal.ZERO);
            vo.setAvailableCommission(BigDecimal.ZERO);
            vo.setFrozenCommission(BigDecimal.ZERO);
            return vo;
        }

        vo.setTotalCommission(d.getTotalCommission());
        vo.setAvailableCommission(d.getAvailableCommission());
        vo.setFrozenCommission(d.getFrozenCommission());
        return vo;
    }

    @Override
    @Transactional
    public void settleCommission() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(freezeDays);
        List<CommissionRecord> records = commissionRecordMapper.selectList(
                new LambdaQueryWrapper<CommissionRecord>()
                        .eq(CommissionRecord::getStatus, CommissionStatus.FROZEN)
                        .le(CommissionRecord::getCreateTime, cutoff));

        if (records.isEmpty()) {
            return;
        }

        for (CommissionRecord record : records) {
            Distributor d = distributorMapper.selectOne(
                    new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, record.getDistributorId()));
            if (d == null) {
                continue;
            }

            // frozen → available
            d.setFrozenCommission(d.getFrozenCommission().subtract(record.getCommissionAmount()));
            d.setAvailableCommission(d.getAvailableCommission().add(record.getCommissionAmount()));
            distributorMapper.updateById(d);

            record.setStatus(CommissionStatus.SETTLED);
            record.setSettleTime(LocalDateTime.now());
            commissionRecordMapper.updateById(record);
        }

        log.info("佣金解冻完成，处理 {} 条记录", records.size());
    }

    @Override
    public PageResult<CommissionRecord> getList(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<CommissionRecord> wrapper = new LambdaQueryWrapper<CommissionRecord>()
                .eq(userId != null, CommissionRecord::getDistributorId, userId)
                .eq(status != null, CommissionRecord::getStatus, status)
                .orderByDesc(CommissionRecord::getCreateTime);
        Page<CommissionRecord> page = commissionRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }
}

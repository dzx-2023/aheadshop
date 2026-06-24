package com.aheadshop.distribution.service.impl;

import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.distribution.domain.dto.WithdrawApplyDTO;
import com.aheadshop.distribution.domain.dto.WithdrawAuditDTO;
import com.aheadshop.distribution.domain.po.Distributor;
import com.aheadshop.distribution.domain.po.WithdrawalRecord;
import com.aheadshop.distribution.enums.WithdrawalStatus;
import com.aheadshop.distribution.mapper.DistributorMapper;
import com.aheadshop.distribution.mapper.WithdrawalRecordMapper;
import com.aheadshop.distribution.service.IWithdrawalService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl extends ServiceImpl<WithdrawalRecordMapper, WithdrawalRecord>
        implements IWithdrawalService {

    private final DistributorMapper distributorMapper;

    @Value("${distribution.commission.min-withdraw-amount:100.00}")
    private BigDecimal minWithdrawAmount;

    @Override
    @Transactional
    public void apply(Long userId, WithdrawApplyDTO dto) {
        // 1. 校验分销商存在且状态正常
        Distributor d = distributorMapper.selectOne(
                new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, userId));
        if (d == null || d.getStatus() != 1) {
            throw new BusinessException(BusinessExceptionCode.DISTRIBUTOR_NOT_FOUND, "您不是有效分销商");
        }

        BigDecimal amount = dto.getAmount();

        // 2. 校验金额 >= 最低提现额
        if (amount.compareTo(minWithdrawAmount) < 0) {
            throw new BusinessException(BusinessExceptionCode.WITHDRAW_MIN_AMOUNT,
                    "最低提现金额为 " + minWithdrawAmount + " 元");
        }

        // 3. 校验 available_commission >= amount
        if (d.getAvailableCommission().compareTo(amount) < 0) {
            throw new BusinessException(BusinessExceptionCode.COMMISSION_NOT_ENOUGH, "可提现余额不足");
        }

        // 4. available → frozen（冻结提现金额）
        d.setAvailableCommission(d.getAvailableCommission().subtract(amount));
        d.setFrozenCommission(d.getFrozenCommission().add(amount));
        distributorMapper.updateById(d);

        // 5. 创建 withdrawal_record（status=PENDING）
        WithdrawalRecord record = new WithdrawalRecord();
        record.setDistributorId(userId);
        record.setAmount(amount);
        record.setStatus(WithdrawalStatus.PENDING);
        this.save(record);

        log.info("用户 {} 申请提现 {} 元，记录ID={}", userId, amount, record.getId());
    }

    @Override
    @Transactional
    public void audit(WithdrawAuditDTO dto) {
        WithdrawalRecord record = this.getById(dto.getId());
        if (record == null) {
            throw new BusinessException(BusinessExceptionCode.NOT_FOUND, "提现记录不存在");
        }
        if (record.getStatus() != WithdrawalStatus.PENDING) {
            throw new BusinessException(BusinessExceptionCode.ORDER_STATUS_ERROR, "该提现已处理");
        }

        Distributor d = distributorMapper.selectOne(
                new LambdaQueryWrapper<Distributor>().eq(Distributor::getUserId, record.getDistributorId()));

        if (dto.getApproved()) {
            // 通过：status=APPROVED，扣减 frozenCommission（表示资金已划出）
            record.setStatus(WithdrawalStatus.APPROVED);
            record.setAuditTime(LocalDateTime.now());
            record.setRemark(dto.getRemark());
            this.updateById(record);

            if (d != null) {
                d.setFrozenCommission(d.getFrozenCommission().subtract(record.getAmount()));
                distributorMapper.updateById(d);
            }
            log.info("提现 {} 审核通过，扣减冻结余额 {}", record.getId(), record.getAmount());
        } else {
            // 拒绝：frozen → available（退回）, status=REJECTED
            record.setStatus(WithdrawalStatus.REJECTED);
            record.setAuditTime(LocalDateTime.now());
            record.setRemark(dto.getRemark());
            this.updateById(record);

            if (d != null) {
                d.setFrozenCommission(d.getFrozenCommission().subtract(record.getAmount()));
                d.setAvailableCommission(d.getAvailableCommission().add(record.getAmount()));
                distributorMapper.updateById(d);
            }
            log.info("提现 {} 审核拒绝，金额 {} 退回", record.getId(), record.getAmount());
        }
    }

    @Override
    @Transactional
    public void confirmPaid(Long id) {
        WithdrawalRecord record = this.getById(id);
        if (record == null) {
            throw new BusinessException(BusinessExceptionCode.NOT_FOUND, "提现记录不存在");
        }
        if (record.getStatus() != WithdrawalStatus.APPROVED) {
            throw new BusinessException(BusinessExceptionCode.ORDER_STATUS_ERROR, "只有已通过的提现已能确认打款");
        }
        record.setStatus(WithdrawalStatus.PAID);
        record.setPayTime(LocalDateTime.now());
        this.updateById(record);
        log.info("提现 {} 已确认打款", id);
    }

    @Override
    public PageResult<WithdrawalRecord> getList(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<WithdrawalRecord> wrapper = new LambdaQueryWrapper<WithdrawalRecord>()
                .eq(userId != null, WithdrawalRecord::getDistributorId, userId)
                .eq(status != null, WithdrawalRecord::getStatus, status)
                .orderByDesc(WithdrawalRecord::getCreateTime);
        Page<WithdrawalRecord> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getTotal(), page.getRecords());
    }
}

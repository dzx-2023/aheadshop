package com.aheadshop.user.service.impl;

import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.user.domain.dto.AddressDTO;
import com.aheadshop.user.domain.po.UserAddress;
import com.aheadshop.user.domain.vo.AddressVO;
import com.aheadshop.user.mapper.UserAddressMapper;
import com.aheadshop.user.service.IAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IAddressService {

    private static final int MAX_ADDRESS_COUNT = 20;

    @Override
    public List<AddressVO> listByUserId(Long userId) {
        List<UserAddress> list = this.list(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getUpdateTime)
        );
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addAddress(Long userId, AddressDTO dto) {
        long count = this.count(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
        if (count >= MAX_ADDRESS_COUNT) {
            throw new BusinessException(BusinessExceptionCode.ADDRESS_LIMIT, "收货地址最多" + MAX_ADDRESS_COUNT + "个");
        }
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setIsDefault(count == 0 ? 1 : 0);
        this.save(address);
    }

    @Override
    @Transactional
    public void updateAddress(Long userId, AddressDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(BusinessExceptionCode.PARAM_ERROR, "地址 ID 不能为空");
        }
        UserAddress address = this.getById(dto.getId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(BusinessExceptionCode.NOT_FOUND, "地址不存在");
        }
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        this.updateById(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(BusinessExceptionCode.NOT_FOUND, "地址不存在");
        }
        this.removeById(addressId);
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        UserAddress address = this.getById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(BusinessExceptionCode.NOT_FOUND, "地址不存在");
        }
        // 取消当前默认
        this.update(new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1)
                .set(UserAddress::getIsDefault, 0));
        // 设置新默认
        address.setIsDefault(1);
        this.updateById(address);
    }

    @Override
    public AddressVO getByIdForInner(Long addressId) {
        UserAddress address = this.getById(addressId);
        if (address == null) {
            return null;
        }
        return toVO(address);
    }

    private AddressVO toVO(UserAddress a) {
        return AddressVO.builder()
                .id(a.getId())
                .receiverName(a.getReceiverName())
                .receiverPhone(a.getReceiverPhone())
                .province(a.getProvince())
                .city(a.getCity())
                .district(a.getDistrict())
                .detailAddress(a.getDetailAddress())
                .isDefault(a.getIsDefault())
                .build();
    }
}

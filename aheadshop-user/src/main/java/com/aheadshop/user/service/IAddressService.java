package com.aheadshop.user.service;

import com.aheadshop.user.domain.dto.AddressDTO;
import com.aheadshop.user.domain.vo.AddressVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aheadshop.user.domain.po.UserAddress;

import java.util.List;

public interface IAddressService extends IService<UserAddress> {

    List<AddressVO> listByUserId(Long userId);

    void addAddress(Long userId, AddressDTO dto);

    void updateAddress(Long userId, AddressDTO dto);

    void deleteAddress(Long userId, Long addressId);

    void setDefault(Long userId, Long addressId);

    AddressVO getByIdForInner(Long addressId);
}

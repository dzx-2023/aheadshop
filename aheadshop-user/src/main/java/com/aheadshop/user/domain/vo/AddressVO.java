package com.aheadshop.user.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressVO {

    private Long id;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
}

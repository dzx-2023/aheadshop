package com.aheadshop.order.domain.vo;

import lombok.Data;

@Data
public class AddressSnapshotVO {

    private Long id;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
}

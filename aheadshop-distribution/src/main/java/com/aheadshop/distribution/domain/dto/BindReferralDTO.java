package com.aheadshop.distribution.domain.dto;

import lombok.Data;

@Data
public class BindReferralDTO {

    private Long userId;
    private String inviteCode;
    private Long referrerUserId;
}

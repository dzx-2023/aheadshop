package com.aheadshop.pay.domain.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaySubmitVO {

    private String payNo;
    private String payForm;
}

package com.aheadshop.user.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateUserDTO {

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    @Min(value = 0, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    private Integer gender;
}
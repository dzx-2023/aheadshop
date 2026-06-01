package com.aheadshop.admin.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPageVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String avatar;
    private Integer gender;
    private Integer status;
    private LocalDateTime createTime;
}

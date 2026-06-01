package com.aheadshop.admin.feign;

import com.aheadshop.admin.domain.vo.UserPageVO;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "aheadshop-user", path = "/inner/user")
public interface UserFeignClient {

    @GetMapping("/list")
    Result<PageResult<UserPageVO>> listUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam("pageNum") Integer pageNum,
            @RequestParam("pageSize") Integer pageSize);

    @PutMapping("/status")
    Result<Void> updateStatus(@RequestParam("userId") Long userId,
                              @RequestParam("status") Integer status);

    @GetMapping("/today-count")
    Result<Long> todayNewUserCount();
}

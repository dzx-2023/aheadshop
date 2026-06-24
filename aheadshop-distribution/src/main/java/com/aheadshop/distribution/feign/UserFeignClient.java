package com.aheadshop.distribution.feign;

import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "aheadshop-user", path = "/inner/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<Map<String, Object>> getUserById(@PathVariable("id") Long userId);

    @GetMapping("/by-invite-code/{code}")
    Result<Map<String, Object>> getByInviteCode(@PathVariable("code") String inviteCode);
}

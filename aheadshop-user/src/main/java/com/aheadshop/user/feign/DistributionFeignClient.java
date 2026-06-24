package com.aheadshop.user.feign;

import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "aheadshop-distribution", path = "/inner/distribution")
public interface DistributionFeignClient {

    @PostMapping("/generate-code/{userId}")
    Result<String> generateInviteCode(@PathVariable("userId") Long userId);

    /** 绑定推荐关系，返回推荐人 userId */
    @PostMapping("/bind-referral")
    Result<Long> bindReferral(@RequestParam("userId") Long userId,
                              @RequestParam("inviteCode") String inviteCode);

    @GetMapping("/exists/{userId}")
    Result<Boolean> exists(@PathVariable("userId") Long userId);
}

package com.aheadshop.order.feign;

import com.aheadshop.common.core.result.Result;
import com.aheadshop.order.domain.vo.AddressSnapshotVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "aheadshop-user", path = "/inner/user")
public interface UserFeignClient {

    @GetMapping("/address/{id}")
    Result<AddressSnapshotVO> getAddress(@PathVariable("id") Long addressId);
}

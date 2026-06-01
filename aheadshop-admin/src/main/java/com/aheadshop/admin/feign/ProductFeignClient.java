package com.aheadshop.admin.feign;

import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "aheadshop-product", path = "/inner/product")
public interface ProductFeignClient {

    @GetMapping("/hot-products")
    Result<List<Map<String, Object>>> hotProducts(@RequestParam(value = "limit", defaultValue = "10") Integer limit);
}

package com.aheadshop.admin.feign;

import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(value = "aheadshop-product", path = "/product/background")
public interface BackgroundFeignClient {

    @GetMapping("/list")
    Result<List<Map<String, Object>>> listAll();

    @DeleteMapping("/{id}")
    Result<Void> delete(@PathVariable("id") Long id);

    @PutMapping("/{id}/sort")
    Result<Void> updateSort(@PathVariable("id") Long id,
                            @RequestParam("sortOrder") Integer sortOrder);

    @PutMapping("/{id}/enabled")
    Result<Void> updateEnabled(@PathVariable("id") Long id,
                               @RequestParam("enabled") Integer enabled);
}

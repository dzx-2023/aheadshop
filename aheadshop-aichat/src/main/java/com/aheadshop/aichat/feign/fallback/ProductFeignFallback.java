package com.aheadshop.aichat.feign.fallback;

import com.aheadshop.aichat.feign.ProductFeignClient;
import com.aheadshop.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 商品服务降级工厂
 */
@Slf4j
@Component
public class ProductFeignFallback implements FallbackFactory<ProductFeignClient> {

    @Override
    public ProductFeignClient create(Throwable cause) {
        log.error("商品服务调用失败，触发降级", cause);
        return new ProductFeignClient() {
            @Override
            public Result<?> searchProducts(String keyword, Integer status, Integer pageNum, Integer pageSize) {
                log.warn("商品搜索降级: keyword={}", keyword);
                return Result.success(null);
            }

            @Override
            public Result<?> getProduct(Long id) {
                log.warn("商品详情降级: id={}", id);
                return Result.success(null);
            }
        };
    }
}

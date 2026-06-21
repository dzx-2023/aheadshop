package com.aheadshop.aichat.feign;

import com.aheadshop.aichat.feign.fallback.ProductFeignFallback;
import com.aheadshop.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 商品服务 Feign 客户端 —— 用于 AI 客服 RAG 知识检索
 */
@FeignClient(name = "aheadshop-product", fallbackFactory = ProductFeignFallback.class)
public interface ProductFeignClient {

    /**
     * 搜索商品（按关键词）
     * 复用商品服务已有的 /product/spu/list 接口
     */
    @GetMapping("/product/spu/list")
    Result<?> searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "status", defaultValue = "1") Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    );

    /**
     * 获取单个商品详情
     */
    @GetMapping("/product/spu/{id}")
    Result<?> getProduct(@PathVariable("id") Long id);
}

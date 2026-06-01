package com.aheadshop.cart.service;

import cn.hutool.json.JSONUtil;
import com.aheadshop.cart.domain.CartItem;
import com.aheadshop.cart.feign.ProductFeignClient;
import com.aheadshop.common.core.result.Result;
import com.aheadshop.common.core.vo.SkuInfoVO;
import com.aheadshop.common.redis.util.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final StringRedisTemplate redisTemplate;
    private final ProductFeignClient productFeignClient;

    private static final String CART_KEY_PREFIX = "cart:user:";

    public void addItem(Long userId, Long skuId, Integer quantity) {
        String key = CART_KEY_PREFIX + userId;
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        String existing = ops.get(key, String.valueOf(skuId));
        if (existing != null) {
            // 已有该 SKU，数量叠加
            CartItem item = JSONUtil.toBean(existing, CartItem.class);
            item.setQuantity(item.getQuantity() + quantity);
            ops.put(key, String.valueOf(skuId), JSONUtil.toJsonStr(item));
        } else {
            // 新增：调商品服务查 SKU 信息
            Result<SkuInfoVO> result = productFeignClient.getSkuInfo(skuId);
            if (result == null || result.getCode() != 200 || result.getData() == null) {
                throw new RuntimeException("商品不存在");
            }
            SkuInfoVO skuInfo = result.getData();
            CartItem item = CartItem.builder()
                    .skuId(skuId)
                    .skuName(skuInfo.getSkuName())
                    .image(skuInfo.getImage())
                    .price(skuInfo.getPrice())
                    .specs(skuInfo.getSpecs())
                    .quantity(quantity)
                    .checked(1)
                    .build();
            ops.put(key, String.valueOf(skuId), JSONUtil.toJsonStr(item));
        }
    }

    public List<CartItem> list(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Map<String, String> entries = ops.entries(key);

        if (entries.isEmpty()) {
            return new ArrayList<>();
        }

        List<CartItem> items = entries.values().stream()
                .map(json -> JSONUtil.toBean(json, CartItem.class))
                .collect(Collectors.toList());

        // 批量刷新最新价格
        try {
            for (CartItem item : items) {
                Result<SkuInfoVO> result = productFeignClient.getSkuInfo(item.getSkuId());
                if (result != null && result.getCode() == 200 && result.getData() != null) {
                    item.setPrice(result.getData().getPrice());
                    item.setSkuName(result.getData().getSkuName());
                    item.setImage(result.getData().getImage());
                }
            }
            // 回写更新后的数据
            for (CartItem item : items) {
                ops.put(key, String.valueOf(item.getSkuId()), JSONUtil.toJsonStr(item));
            }
        } catch (Exception e) {
            log.warn("刷新购物车商品价格失败: {}", e.getMessage());
        }

        return items;
    }

    public void updateQuantity(Long userId, Long skuId, Integer quantity) {
        String key = CART_KEY_PREFIX + userId;
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        String existing = ops.get(key, String.valueOf(skuId));
        if (existing == null) {
            throw new RuntimeException("购物车中无此商品");
        }
        CartItem item = JSONUtil.toBean(existing, CartItem.class);
        item.setQuantity(quantity);
        ops.put(key, String.valueOf(skuId), JSONUtil.toJsonStr(item));
    }

    public void deleteItem(Long userId, Long skuId) {
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.opsForHash().delete(key, String.valueOf(skuId));
    }

    public void checkItem(Long userId, Long skuId, Integer checked) {
        String key = CART_KEY_PREFIX + userId;
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        String existing = ops.get(key, String.valueOf(skuId));
        if (existing == null) {
            throw new RuntimeException("购物车中无此商品");
        }
        CartItem item = JSONUtil.toBean(existing, CartItem.class);
        item.setChecked(checked);
        ops.put(key, String.valueOf(skuId), JSONUtil.toJsonStr(item));
    }

    public List<CartItem> getCheckedItems(Long userId) {
        return list(userId).stream()
                .filter(item -> item.getChecked() != null && item.getChecked() == 1)
                .collect(Collectors.toList());
    }

    public Long count(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        Long size = redisTemplate.opsForHash().size(key);
        return size == null ? 0L : size;
    }

    public void clearCheckedItems(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        Map<String, String> entries = ops.entries(key);
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            CartItem item = JSONUtil.toBean(entry.getValue(), CartItem.class);
            if (item.getChecked() != null && item.getChecked() == 1) {
                ops.delete(key, entry.getKey());
            }
        }
    }
}

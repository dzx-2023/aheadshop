package com.aheadshop.admin.controller;

import com.aheadshop.admin.domain.vo.DashboardVO;
import com.aheadshop.admin.feign.OrderFeignClient;
import com.aheadshop.admin.feign.ProductFeignClient;
import com.aheadshop.admin.feign.UserFeignClient;
import com.aheadshop.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "管理端-仪表盘")
public class DashboardController {

    private final OrderFeignClient orderFeignClient;
    private final UserFeignClient userFeignClient;
    private final ProductFeignClient productFeignClient;

    @Operation(summary = "总览数据")
    @GetMapping("/overview")
    public Result<DashboardVO> overview() {
        Long todayOrderCount = 0L;
        BigDecimal todaySalesAmount = BigDecimal.ZERO;
        Long todayNewUsers = 0L;
        Long pendingShipCount = 0L;
        List<Map<String, Object>> salesTrend = Collections.emptyList();
        List<Map<String, Object>> hotProducts = Collections.emptyList();

        try {
            todayOrderCount = orderFeignClient.todayOrderCount().getData();
        } catch (Exception e) {
            log.warn("获取今日订单数失败: {}", e.getMessage());
        }

        try {
            todaySalesAmount = orderFeignClient.todaySalesAmount().getData();
        } catch (Exception e) {
            log.warn("获取今日销售额失败: {}", e.getMessage());
        }

        try {
            todayNewUsers = userFeignClient.todayNewUserCount().getData();
        } catch (Exception e) {
            log.warn("获取今日新增用户失败: {}", e.getMessage());
        }

        try {
            pendingShipCount = orderFeignClient.pendingShipCount().getData();
        } catch (Exception e) {
            log.warn("获取待发货数失败: {}", e.getMessage());
        }

        try {
            salesTrend = orderFeignClient.salesTrend().getData();
        } catch (Exception e) {
            log.warn("获取销售趋势失败: {}", e.getMessage());
        }

        try {
            hotProducts = productFeignClient.hotProducts(10).getData();
        } catch (Exception e) {
            log.warn("获取热销商品失败: {}", e.getMessage());
        }

        DashboardVO dashboard = DashboardVO.builder()
                .todayOrderCount(todayOrderCount != null ? todayOrderCount : 0L)
                .todaySalesAmount(todaySalesAmount != null ? todaySalesAmount : BigDecimal.ZERO)
                .todayNewUsers(todayNewUsers != null ? todayNewUsers : 0L)
                .pendingShipCount(pendingShipCount != null ? pendingShipCount : 0L)
                .salesTrend(salesTrend != null ? salesTrend : Collections.emptyList())
                .hotProducts(hotProducts != null ? hotProducts : Collections.emptyList())
                .build();

        return Result.success(dashboard);
    }
}

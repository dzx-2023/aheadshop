package com.aheadshop.admin.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardVO {

    private Long todayOrderCount;
    private BigDecimal todaySalesAmount;
    private Long todayNewUsers;
    private Long pendingShipCount;
    private List<Map<String, Object>> salesTrend;
    private List<Map<String, Object>> hotProducts;
}

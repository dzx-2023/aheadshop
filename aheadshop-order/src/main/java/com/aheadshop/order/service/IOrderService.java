package com.aheadshop.order.service;

import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.order.domain.dto.CreateOrderDTO;
import com.aheadshop.order.domain.dto.OrderQueryDTO;
import com.aheadshop.order.domain.vo.OrderDetailVO;
import com.aheadshop.order.domain.vo.OrderPageVO;
import com.aheadshop.order.domain.vo.SubmitOrderVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IOrderService {

    SubmitOrderVO createOrder(Long userId, CreateOrderDTO dto);

    PageResult<OrderPageVO> listOrders(Long userId, OrderQueryDTO dto);

    OrderDetailVO getOrderDetail(Long userId, String orderNo);

    void cancelOrder(Long userId, String orderNo);

    void confirmReceive(Long userId, String orderNo);

    OrderDetailVO getOrderByOrderNo(String orderNo);

    void paySuccess(String orderNo);

    // 管理端接口
    PageResult<OrderPageVO> adminListOrders(Integer status, Integer pageNum, Integer pageSize);

    void shipOrder(String orderNo);

    Long todayOrderCount();

    BigDecimal todaySalesAmount();

    Long pendingShipCount();

    List<Map<String, Object>> salesTrend();
}

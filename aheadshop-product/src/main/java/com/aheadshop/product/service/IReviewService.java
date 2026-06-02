package com.aheadshop.product.service;

import com.aheadshop.product.domain.dto.ReviewDTO;
import com.aheadshop.product.domain.po.Review;
import com.aheadshop.product.domain.vo.ReviewVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IReviewService extends IService<Review> {

    void addReview(Long userId, ReviewDTO dto);

    IPage<ReviewVO> listBySpuId(Long spuId, int pageNum, int pageSize);

    Double getAvgScore(Long spuId);

    IPage<ReviewVO> listByUserId(Long userId, int pageNum, int pageSize);

    boolean hasReviewed(String orderNo);
}

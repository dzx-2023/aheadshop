package com.aheadshop.product.service.impl;

import com.aheadshop.product.domain.dto.ReviewDTO;
import com.aheadshop.product.domain.po.Review;
import com.aheadshop.product.domain.vo.ReviewVO;
import com.aheadshop.product.mapper.ReviewMapper;
import com.aheadshop.product.service.IReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements IReviewService {

    @Override
    public void addReview(Long userId, ReviewDTO dto) {
        Review review = new Review();
        review.setUserId(userId);
        review.setSpuId(dto.getSpuId());
        review.setSkuId(dto.getSkuId());
        review.setOrderNo(dto.getOrderNo());
        review.setScore(dto.getScore());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());
        review.setIsAnonymous(dto.getIsAnonymous() != null ? dto.getIsAnonymous() : 0);
        review.setStatus(1);
        this.save(review);
    }

    @Override
    public IPage<ReviewVO> listBySpuId(Long spuId, int pageNum, int pageSize) {
        IPage<Review> page = this.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getSpuId, spuId)
                        .eq(Review::getStatus, 1)
                        .orderByDesc(Review::getCreateTime)
        );
        return page.convert(r -> ReviewVO.builder()
                .id(r.getId())
                .userId(r.getIsAnonymous() == 1 ? null : r.getUserId())
                .nickname(r.getIsAnonymous() == 1 ? "匿名用户" : null)
                .score(r.getScore())
                .content(r.getContent())
                .images(r.getImages())
                .isAnonymous(r.getIsAnonymous())
                .reply(r.getReply())
                .createTime(r.getCreateTime())
                .build());
    }

    @Override
    public Double getAvgScore(Long spuId) {
        return baseMapper.selectAvgScore(spuId);
    }

    @Override
    public IPage<ReviewVO> listByUserId(Long userId, int pageNum, int pageSize) {
        IPage<Review> page = this.page(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getUserId, userId)
                        .eq(Review::getStatus, 1)
                        .orderByDesc(Review::getCreateTime)
        );
        return page.convert(r -> ReviewVO.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .spuId(r.getSpuId())
                .skuId(r.getSkuId())
                .orderNo(r.getOrderNo())
                .score(r.getScore())
                .content(r.getContent())
                .images(r.getImages())
                .isAnonymous(r.getIsAnonymous())
                .reply(r.getReply())
                .createTime(r.getCreateTime())
                .build());
    }

    @Override
    public boolean hasReviewed(String orderNo) {
        return this.count(new LambdaQueryWrapper<Review>()
                .eq(Review::getOrderNo, orderNo)
                .eq(Review::getStatus, 1)) > 0;
    }
}

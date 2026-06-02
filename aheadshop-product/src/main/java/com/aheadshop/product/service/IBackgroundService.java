package com.aheadshop.product.service;

import com.aheadshop.product.domain.po.Background;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBackgroundService {

    List<Background> listAll();

    List<Background> listActive();

    Background upload(MultipartFile file);

    void deleteById(Long id);

    void updateSort(Long id, Integer sortOrder);

    void updateEnabled(Long id, Integer enabled);
}

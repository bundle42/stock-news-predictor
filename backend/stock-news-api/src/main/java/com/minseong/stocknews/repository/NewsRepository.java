package com.minseong.stocknews.repository;

import com.minseong.stocknews.domain.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByStockCode(String stockCode);

    boolean existsByLink(String link);
}

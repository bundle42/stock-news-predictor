package com.minseong.stocknews.service;

import com.minseong.stocknews.controller.dto.NewsCreateRequest;
import com.minseong.stocknews.domain.entity.News;
import com.minseong.stocknews.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    // 생성자 주입 (Lombok 없이 정석)
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    // 종목 코드로 뉴스 조회
    public List<News> getNewsByStockCode(String stockCode) {
        return newsRepository.findByStockCode(stockCode);
    }

    // 뉴스 1건 저장 (중복 링크 방지)
    public boolean saveNews(News news) {
        if (newsRepository.existsByLink(news.getLink())) {
            return false;
        }
        newsRepository.save(news);
        return true;
    }

    // 여러 뉴스 저장 (크롤링용)
    public int saveAllIfNotExists(List<News> newsList) {
        int savedCount = 0;

        for (News news : newsList) {
            if (!newsRepository.existsByLink(news.getLink())) {
                newsRepository.save(news);
                savedCount++;
            }
        }

        return savedCount;
    }

    // API 용 뉴스 저장
    public News createNewsFromDto(NewsCreateRequest dto) {
        if (newsRepository.existsByLink(dto.getLink())) {
            throw new IllegalArgumentException("이미 존재");
        }

        News news = new News(
                dto.getTitle(),
                dto.getSummary(),
                dto.getContent(),
                dto.getLink(),
                dto.getPublishedAt(),
                dto.getStockCode(),
                dto.getSource(),
                dto.getSentiment()
        );

        return newsRepository.save(news);
    }

}

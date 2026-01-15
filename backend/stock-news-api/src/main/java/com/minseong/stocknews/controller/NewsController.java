package com.minseong.stocknews.controller;

import com.minseong.stocknews.controller.dto.NewsCreateRequest;
import com.minseong.stocknews.controller.dto.NewsResponse;
import com.minseong.stocknews.domain.entity.News;
import com.minseong.stocknews.repository.NewsRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsRepository newsRepository;

    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    // 전체 뉴스 조회
    @GetMapping
    public List<NewsResponse> getAllNews() {
        return newsRepository.findAll()
                .stream()
                .map(NewsResponse::new)
                .toList();
    }


    // 종목별 뉴스 조회
    @GetMapping("/stock/{stockCode}")
    public List<News> getNewsByStock(@PathVariable String stockCode) {
        return newsRepository.findByStockCode(stockCode);
    }

    // 뉴스 저장
    @PostMapping
    public News createNews(@RequestBody NewsCreateRequest request) {

        if (newsRepository.existsByLink(request.getLink())) {
            throw new IllegalArgumentException("이미 존재하는 뉴스 링크입니다.");
        }

        News news = new News(
                request.getTitle(),
                request.getSummary(),
                request.getContent(),
                request.getLink(),
                request.getPublishedAt(),
                request.getStockCode(),
                request.getSource(),
                request.getSentiment()
        );

        return newsRepository.save(news);
    }


}

package com.minseong.stocknews.controller.dto;

import com.minseong.stocknews.domain.entity.News;

import java.time.LocalDateTime;

public class NewsResponse {

    private Long id;
    private String title;
    private String summary;
    private String link;
    private String stockCode;
    private String source;
    private Integer sentiment;
    private LocalDateTime publishedAt;

    public NewsResponse(News news) {
        this.id = news.getId();
        this.title = news.getTitle();
        this.summary = news.getSummary();
        this.link = news.getLink();
        this.stockCode = news.getStockCode();
        this.source = news.getSource();
        this.sentiment = news.getSentiment();
        this.publishedAt = news.getPublishedAt();
    }

    // Getter들
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getLink() { return link; }
    public String getStockCode() { return stockCode; }
    public String getSource() { return source; }
    public Integer getSentiment() { return sentiment; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
}

package com.minseong.stocknews.controller.dto;

import java.time.LocalDateTime;

public class NewsCreateRequest {

    private String title;
    private String summary;
    private String content;
    private String link;
    private LocalDateTime publishedAt;
    private String stockCode;
    private String source;
    private Integer sentiment;

    // 기본 생성자 (JSON → 객체)
    public NewsCreateRequest() {
    }

    // Getter만
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getContent() { return content; }
    public String getLink() { return link; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public String getStockCode() { return stockCode; }
    public String getSource() { return source; }
    public Integer getSentiment() { return sentiment; }
}

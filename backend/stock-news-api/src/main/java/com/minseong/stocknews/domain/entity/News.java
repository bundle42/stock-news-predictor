package com.minseong.stocknews.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, unique = true)
    private String link;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private String stockCode;

    private String source;

    private Integer sentiment;

    private LocalDateTime createdAt;

    // JPA 필수: 기본 생성자 (protected or public)
    protected News() {
    }

    // 생성자 (필요한 값만)
    public News(
            String title,
            String summary,
            String content,
            String link,
            LocalDateTime publishedAt,
            String stockCode,
            String source,
            Integer sentiment
    ) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.link = link;
        this.publishedAt = publishedAt;
        this.stockCode = stockCode;
        this.source = source;
        this.sentiment = sentiment;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    // ✅ Getter들 (Setter 없음)

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getContent() {
        return content;
    }

    public String getLink() {
        return link;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getSource() {
        return source;
    }

    public Integer getSentiment() {
        return sentiment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

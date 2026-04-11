package com.example.board.board.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.board.board.service.NaverNewsService;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final NaverNewsService naverNewsService;

    @Scheduled(fixedDelay = 1000 * 60 * 180) // 3시간마다
    public void runNewsCrawling() {
        System.out.println("뉴스 자동 수집 시작 안함");
        // naverNewsService.saveNewsToBoardMultiple("삼성전자", "SK하이닉스", "현대차");
    }
}
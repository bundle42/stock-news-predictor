package com.example.board.board.scheduler;

import com.example.board.board.service.PredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.board.board.service.NaverNewsService;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final NaverNewsService naverNewsService;
    private final PredictService predictService;

    @Scheduled(fixedDelay = 1000 * 60 * 180) // 3시간마다
    public void runNewsCrawling() {
        System.out.println("뉴스 자동 수집 시작 안함(Git Action Test01)");
        // naverNewsService.saveNewsToBoardMultiple("삼성전자", "SK하이닉스", "현대차");
    }

    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void runModelTraining() {
        System.out.println("새벽 1시 입니다. 모델 자동 학습 시작");

        // predictService.train("삼성전자");
        // predictService.train("SK하이닉스");
        // predictService.train("현대차");
    }
}
package com.example.board.board.service;

import com.example.board.board.dto.BoardDTO;
import com.example.board.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PredictService {

    private final BoardRepository boardRepository;
    private final DailyFeatureService dailyFeatureService;

    @Value("${ai.server-url}")
    private String aiServerUrl;

    public Map<String, Object> start(String stockName) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm-ss");

        String startDate = "25-11-24-00-00-00";
        String endDate = LocalDateTime.now().format(formatter);
        String searchQuery = stockName;

        List<BoardDTO> recentNews = boardRepository
                .findByPubDateBetweenAndSearchQueryContainingOrderByPubDateAsc(startDate, endDate, searchQuery)
                .stream()
                .map(BoardDTO::toBoardDTO)
                .toList();

        // 1. 뉴스 데이터 없음 방지
        if (recentNews.isEmpty()) {
            throw new IllegalStateException("예측을 위한 뉴스 데이터가 없습니다.");
        }

        // 중요: 일별 피처 생성
        List<Map<String, Object>> dailyFeatures = dailyFeatureService.makeDailyFeatures(recentNews);

        // 2. feature 데이터 없음 방지
        if (dailyFeatures.isEmpty()) {
            throw new IllegalStateException("예측용 feature 생성에 실패했습니다.");
        }

        System.out.println("===== DAILY FEATURES =====");
        dailyFeatures.forEach(System.out::println);
        System.out.println("==========================");

        String url = aiServerUrl + "/predict";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("stockName", stockName);
        requestBody.put("features", dailyFeatures);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getBody() == null) {
                throw new IllegalStateException("예측 API 응답이 비어 있습니다.");
            }

            return response.getBody();

        } catch (RestClientException e) {
            throw new IllegalStateException("Python 예측 서버 호출 실패: " + e.getMessage(), e);
        }
    }
}
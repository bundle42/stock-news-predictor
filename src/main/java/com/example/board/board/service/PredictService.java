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

    @Value("${app.ai-server-url}")
    private String aiServerUrl;

    public Map<String, Object> start(String stockName) {
        return callAiServer(stockName, "/predict");
    }

    public Map<String, Object> train(String stockName) {
        return callAiServer(stockName, "/train");
    }

    private Map<String, Object> callAiServer(String stockName, String endpoint) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm-ss");

        String startDate = "25-11-24-00-00-00";
        String endDate = LocalDateTime.now().format(formatter);
        String searchQuery = stockName;

        List<BoardDTO> recentNews = boardRepository
                .findByPubDateBetweenAndSearchQueryContainingOrderByPubDateAsc(startDate, endDate, searchQuery)
                .stream()
                .map(BoardDTO::toBoardDTO)
                .toList();

        if (recentNews.isEmpty()) {
            throw new IllegalStateException("뉴스 데이터가 없습니다.");
        }

        List<Map<String, Object>> dailyFeatures =
                dailyFeatureService.makeDailyFeatures(recentNews);

        if (dailyFeatures.isEmpty()) {
            throw new IllegalStateException("feature 생성 실패");
        }

        String url = aiServerUrl + endpoint;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("stockName", stockName);
        requestBody.put("features", dailyFeatures);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() == null) {
            throw new IllegalStateException("응답 없음");
        }

        return response.getBody();
    }
}
package com.example.board.board.service;

import com.example.board.board.dto.BoardDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverNewsService {

    private final BoardService boardService;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${ai.server-url}")
    private String aiServerUrl;

    public void saveNewsToBoardMultiple(String... queries) {
        for (String query : queries) {
            saveNewsToBoard(query);
        }
    }

    public void saveNewsToBoard(String query) {

        try {
            String apiURL = "https://openapi.naver.com/v1/search/news.json?query="
                    + query + "&display=100&sort=date";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(apiURL, HttpMethod.GET, entity, String.class);

            String body = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            JsonNode items = root.get("items");

            for (JsonNode item : items) {

                String title = item.get("title").asText();
                String description = item.get("description").asText();
                String link = item.get("link").asText();

                String pubDate = item.get("pubDate").asText();
                // 1. 문자열 → ZonedDateTime
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, inputFormatter);
                // 2. ZonedDateTime → 원하는 형식 문자열
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm-ss");
                String formattedPubDate = zonedDateTime.format(outputFormatter);

                // HTML 태그 제거
                title = title.replaceAll("<[^>]*>", "");
                description = description.replaceAll("<[^>]*>", "");

                // 파이썬으로 보낸 요청을 받은 응답
                Map<String, Object> result = sendToPythonRead(title);
                System.out.println("Python 응답: " + result);

                String label = (String) result.get("label");
                Double confidence = Double.valueOf(result.get("confidence").toString());
                Double sentimentScore = Double.valueOf(result.get("sentiment_score").toString());

                BoardDTO boardDTO = new BoardDTO();
                boardDTO.setBoardTitle(title);
                boardDTO.setBoardContents(description);
                boardDTO.setNewsLink(link);
                boardDTO.setPubDate(formattedPubDate);
                boardDTO.setSearchQuery(query);
                boardDTO.setMemberId(1L);

                boardDTO.setLabel(label);
                boardDTO.setConfidence(confidence);
                boardDTO.setSentimentScore(sentimentScore);

                boardService.saveFromApi(boardDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 파이썬 호출 코드(코드 항상 동일)
    public Map<String, Object> sendToPythonRead(String content) {

        String url = aiServerUrl + "/analyze";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("content", content);

        HttpEntity<Map<String, String>> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        return response.getBody();
    }
}
package com.example.board.board.service;

import com.example.board.board.dto.BoardDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NaverNewsService {

    private final BoardService boardService;

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${app.ai-server-url}")
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

            List<String> titles = new ArrayList<>();
            List<String> descriptions = new ArrayList<>();
            List<String> links = new ArrayList<>();
            List<String> pubDates = new ArrayList<>();

            for (JsonNode item : items) {

                String title = item.get("title").asText().replaceAll("<[^>]*>", "");
                String description = item.get("description").asText().replaceAll("<[^>]*>", "");
                String link = item.get("link").asText();

                if (link == null || link.trim().isEmpty()) {
                    continue;
                }

                String pubDate = item.get("pubDate").asText();

                DateTimeFormatter inputFormatter =
                        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(pubDate, inputFormatter);

                DateTimeFormatter outputFormatter =
                        DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm-ss");

                String formattedPubDate = zonedDateTime.format(outputFormatter);

                // 리스트에 저장
                titles.add(title);
                descriptions.add(description);
                links.add(link);
                pubDates.add(formattedPubDate);
            }

            List<Map<String, Object>> results = sendToPythonBatch(titles);

            if (results == null || results.size() != titles.size()) {
                System.out.println("Python 응답 이상 발생");
                return;
            }

            int size = Math.min(titles.size(), results.size());
            for (int i = 0; i < size; i++) {

                Map<String, Object> result = results.get(i);

                String label = (String) result.get("label");
                Double confidence = Double.valueOf(result.get("confidence").toString());
                Double sentimentScore = Double.valueOf(result.get("sentiment_score").toString());

                BoardDTO boardDTO = new BoardDTO();
                boardDTO.setBoardTitle(titles.get(i));
                boardDTO.setBoardContents(descriptions.get(i));
                boardDTO.setNewsLink(links.get(i));
                boardDTO.setPubDate(pubDates.get(i));
                boardDTO.setSearchQuery(query);
                boardDTO.setMemberId(1L);

                boardDTO.setLabel(label);
                boardDTO.setConfidence(confidence);
                boardDTO.setSentimentScore(sentimentScore);

                boardService.saveFromApi(boardDTO);
            }

            System.out.println("뉴스 개수: " + titles.size());
            System.out.println("Python 결과 개수: " + results.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 파이썬 호출 코드(배치로 여러개 한번에)
    public List<Map<String, Object>> sendToPythonBatch(List<String> contents) {

        String url = aiServerUrl + "/analyze-batch";

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 5초
        factory.setReadTimeout(60000);    // 60포

        RestTemplate restTemplate = new RestTemplate(factory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", contents);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<List> response =
                restTemplate.postForEntity(url, request, List.class);

        return response.getBody();
    }
}
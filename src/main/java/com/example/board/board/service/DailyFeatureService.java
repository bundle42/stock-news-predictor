package com.example.board.board.service;

import com.example.board.board.dto.BoardDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyFeatureService {

    private final DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm-ss");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy-MM-dd");

    public List<Map<String, Object>> makeDailyFeatures(List<BoardDTO> boardTableData) {

        if (boardTableData == null || boardTableData.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<BoardDTO>> groupedByDate = boardTableData.stream()
                .collect(Collectors.groupingBy(
                        board -> extractDateOnly(board.getPubDate()),
                        TreeMap::new,
                        Collectors.toList()
                ));

        // 시작일 ~ 종료일 계산
        LocalDate startDate = LocalDate.parse(extractDateOnly(boardTableData.get(0).getPubDate()), dateFormatter);
        LocalDate endDate = LocalDate.parse(extractDateOnly(boardTableData.get(boardTableData.size() - 1).getPubDate()), dateFormatter);

        List<Map<String, Object>> dailyFeatures = new ArrayList<>();

        for (LocalDate current = startDate; !current.isAfter(endDate); current = current.plusDays(1)) {
            String date = current.format(dateFormatter);
            List<BoardDTO> group = groupedByDate.getOrDefault(date, new ArrayList<>());

            int newsCount = group.size();

            long positiveCount = group.stream()
                    .filter(b -> "positive".equalsIgnoreCase(b.getLabel()))
                    .count();

            long negativeCount = group.stream()
                    .filter(b -> "negative".equalsIgnoreCase(b.getLabel()))
                    .count();

            long neutralCount = group.stream()
                    .filter(b -> "neutral".equalsIgnoreCase(b.getLabel()))
                    .count();

            double sentimentMean = group.stream()
                    .mapToDouble(BoardDTO::getSentimentScore)
                    .average()
                    .orElse(0);

            double sentimentSum = group.stream()
                    .mapToDouble(BoardDTO::getSentimentScore)
                    .sum();

            double sentimentStd = calculateStd(
                    group.stream()
                            .map(BoardDTO::getSentimentScore)
                            .collect(Collectors.toList()),
                    sentimentMean
            );

            double positiveRatio = newsCount > 0 ? (double) positiveCount / newsCount : 0;
            double negativeRatio = newsCount > 0 ? (double) negativeCount / newsCount : 0;
            double neutralRatio = newsCount > 0 ? (double) neutralCount / newsCount : 0;

            double confidenceMean = group.stream()
                    .mapToDouble(BoardDTO::getConfidence)
                    .average()
                    .orElse(0);

            Map<String, Object> dailyFeature = new LinkedHashMap<>();
            dailyFeature.put("date", date);
            dailyFeature.put("news_count", newsCount);
            dailyFeature.put("sentiment_mean", round2(sentimentMean));
            dailyFeature.put("sentiment_std", round2(sentimentStd));
            dailyFeature.put("sentiment_sum", round2(sentimentSum));
            dailyFeature.put("positive_count", positiveCount);
            dailyFeature.put("negative_count", negativeCount);
            dailyFeature.put("neutral_count", neutralCount);
            dailyFeature.put("positive_ratio", round2(positiveRatio));
            dailyFeature.put("negative_ratio", round2(negativeRatio));
            dailyFeature.put("neutral_ratio", round2(neutralRatio));
            dailyFeature.put("confidence_mean", round2(confidenceMean));

            dailyFeatures.add(dailyFeature);
        }

        return dailyFeatures;
    }

    private String extractDateOnly(String pubDate) {
        if (pubDate == null || pubDate.length() < 8) {
            return pubDate;
        }
        return pubDate.substring(0, 8);
    }

    private double calculateStd(List<Double> values, double mean) {
        if (values == null || values.isEmpty()) return 0;

        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0);

        return Math.sqrt(variance);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
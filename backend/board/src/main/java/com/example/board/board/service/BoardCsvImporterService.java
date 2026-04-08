package com.example.board.board.service;

import com.example.board.board.entity.BoardEntity;
import com.example.board.board.repository.BoardRepository;
import com.example.board.member.entity.MemberEntity;
import com.example.board.member.repository.MemberRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardCsvImporterService {

    private static final String CSV_DIR_PATH = "C:/springboot_img/";
    private static final long DEFAULT_MEMBER_ID = 1L;
    private static final int BATCH_SIZE = 500;

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final EntityManager entityManager;

    @Transactional
    public void importCsvToMySQL() {
        MemberEntity member = memberRepository.findById(DEFAULT_MEMBER_ID)
                .orElseThrow(() -> new RuntimeException("기본 회원(ID=1)이 존재하지 않습니다."));

        int totalCount = 0;

        File dir = new File(CSV_DIR_PATH);

        File[] csvFiles = dir.listFiles((d, name) ->
                name.startsWith("news_sentiment") && name.endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) {
            throw new RuntimeException("news_sentiment로 시작하는 CSV 파일이 없습니다.");
        }

        for (File csvFile : csvFiles) {
            System.out.println("불러오는 파일: " + csvFile.getName());

            int fileCount = 0;
            List<BoardEntity> batchList = new ArrayList<>();

            try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
                String[] line;
                boolean isHeader = true;

                while ((line = reader.readNext()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    String date = getValue(line, 0);
                    String title = getValue(line, 1);
                    String label = getValue(line, 2);
                    String confidenceStr = getValue(line, 3);
                    String sentimentScoreStr = getValue(line, 4);
                    String contents = getValue(line, 5);
                    String newsLink = getValue(line, 6);
                    String searchQuery = getValue(line, 7);

                    double sentimentScore = parseDouble(sentimentScoreStr);
                    double sentimentConfidence = parseDouble(confidenceStr);
                    String pubDate = convertDateToPubDate(date);

                    BoardEntity boardEntity = new BoardEntity();
                    boardEntity.setBoardTitle(title);
                    boardEntity.setBoardContents(contents);
                    boardEntity.setBoardHits(0);
                    boardEntity.setFileAttached(0);

                    boardEntity.setMember(member);

                    boardEntity.setSentimentLabel(label);
                    boardEntity.setSentimentScore(sentimentScore);
                    boardEntity.setSentimentConfidence(sentimentConfidence);
                    boardEntity.setNewsLink(newsLink);
                    boardEntity.setPubDate(pubDate);
                    boardEntity.setSearchQuery(searchQuery);

                    batchList.add(boardEntity);

                    if (batchList.size() == BATCH_SIZE) {
                        boardRepository.saveAll(batchList);
                        boardRepository.flush();

                        entityManager.clear(); // 영속성 컨텍스트 비우기

                        totalCount += batchList.size();
                        fileCount += batchList.size();

                        System.out.println(totalCount + "개 저장 완료");

                        batchList.clear();
                    }
                }

                // 남은 데이터 저장
                if (!batchList.isEmpty()) {
                    boardRepository.saveAll(batchList);
                    boardRepository.flush();
                    entityManager.clear();

                    totalCount += batchList.size();
                    fileCount += batchList.size();

                    batchList.clear();
                }

                System.out.println(csvFile.getName() + " 처리 완료! (" + fileCount + "개 저장)");

            } catch (IOException | CsvValidationException e) {
                System.out.println("파일 처리 중 오류: " + csvFile.getName());
                e.printStackTrace();
            }
        }

        System.out.println("모든 CSV → MySQL 저장 완료! 총 " + totalCount + "개 삽입됨.");
    }

    private String getValue(String[] line, int index) {
        if (line.length > index && line[index] != null) {
            return line[index].trim();
        }
        return "";
    }

    private double parseDouble(String value) {
        try {
            if (value == null || value.isBlank()) return 0.0;
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String convertDateToPubDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.format(DateTimeFormatter.ofPattern("yy-MM-dd")) + "-00-00-00";
        } catch (Exception e) {
            return dateStr;
        }
    }
}
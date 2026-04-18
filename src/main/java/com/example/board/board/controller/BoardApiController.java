package com.example.board.board.controller;

import com.example.board.board.dto.BoardDTO;
import com.example.board.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardApiController {

    private final BoardService boardService;

    // 전체 게시글
    @GetMapping
    public List<BoardDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return boardService.findAll(page, size);
    }

    // 상세
    @GetMapping("/{id}")
    public BoardDTO findById(@PathVariable Long id) {
        return boardService.findById(id);
    }

    @GetMapping("/{id}/prediction")
    public Map<String, Object> getPrediction(@PathVariable Long id) {

        BoardDTO board = boardService.findById(id);

        //String prediction = fastApiService.predict(board.getContents());

        return Map.of(
                "board", board,
                "prediction", "prediction"
        );
    }
}
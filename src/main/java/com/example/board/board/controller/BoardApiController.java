package com.example.board.board.controller;

import com.example.board.board.dto.BoardDTO;
import com.example.board.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/my")
    public List<BoardDTO> myBoards(@AuthenticationPrincipal UserDetails user) {
        return boardService.findByMemberEmail(user.getUsername());
    }

    // 상세
    @GetMapping("/{id}")
    public BoardDTO findById(@PathVariable Long id) {
        return boardService.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody BoardDTO boardDTO,
            @AuthenticationPrincipal UserDetails user
    ) {
        boardService.update(id, boardDTO, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails user) {

        boardService.delete(id, user.getUsername());
    }

    // 저장
    @PostMapping("/save")
    public ResponseEntity<?> save(
            @RequestParam String boardTitle,
            @RequestParam String boardContents,
            @RequestParam(required = false) MultipartFile boardFile,
            @AuthenticationPrincipal UserDetails user
    ) throws IOException {

        String email = user.getUsername();

        BoardDTO dto = new BoardDTO();
        dto.setBoardTitle(boardTitle);
        dto.setBoardContents(boardContents);
        dto.setBoardFile(boardFile);

        boardService.save(dto, email);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/duplicates")
    public ResponseEntity<String> deleteDuplicates() {
        boardService.deleteDuplicateBoards();
        return ResponseEntity.ok("중복 게시글 삭제 완료");
    }
}
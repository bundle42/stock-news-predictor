package com.example.board.board.controller;

import com.example.board.board.dto.CommentDTO;
import com.example.board.board.service.CommentService;
import com.example.board.member.entity.MemberEntity;
import com.example.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<?> save(
            @RequestBody CommentDTO dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        String email = user.getUsername();
        MemberEntity member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        commentService.save(dto, member);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<List<CommentDTO>> findAll(@PathVariable Long boardId) {
        List<CommentDTO> list = commentService.findAll(boardId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        String email = user.getUsername();

        MemberEntity member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        commentService.delete(id, member.getId());

        return ResponseEntity.ok().build();
    }
}
package com.example.board.member.controller;

import com.example.board.board.service.BoardCsvImporterService;
import com.example.board.board.service.NaverNewsService;
import com.example.board.member.dto.MemberDTO;
import com.example.board.member.dto.RoleChangeRequest;
import com.example.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final MemberService memberService;
    private final NaverNewsService naverNewsService;
    private final BoardCsvImporterService boardCsvImporterService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/news/import")
    public ResponseEntity<?> importNews() {
        naverNewsService.saveNewsToBoardMultiple(
                "삼성전자", "SK하이닉스", "현대차"
        );
        return ResponseEntity.ok("success");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/news/csvimport")
    public ResponseEntity<?> importCsvNews() {
        boardCsvImporterService.importCsvToMySQL();
        return ResponseEntity.ok("success");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members")
    public ResponseEntity<List<MemberDTO>> getMembers() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberService.deleteById(id);
        return ResponseEntity.ok("deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/members/{id}/role")
    public ResponseEntity<?> changeRole(
            @PathVariable Long id,
            @RequestBody RoleChangeRequest req
    ) {
        memberService.changeRole(id, req.getRole());
        return ResponseEntity.ok().build();
    }
}
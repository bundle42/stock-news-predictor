package com.example.board.member.controller;

import com.example.board.member.dto.MemberDTO;
import com.example.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final MemberService memberService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members")
    public List<MemberDTO> getMembers() {
        return memberService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/members/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteById(id);
    }
}
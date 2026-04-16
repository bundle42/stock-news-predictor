package com.example.board.member.controller;

import com.example.board.member.dto.MemberDTO;
import com.example.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/me")
    public MemberDTO me(@AuthenticationPrincipal UserDetails user) {
        return memberService.findByEmail(user.getUsername());
    }
}
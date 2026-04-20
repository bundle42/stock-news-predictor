package com.example.board.member.controller;

import com.example.board.member.dto.LoginDto;
import com.example.board.member.dto.MemberDTO;
import com.example.board.member.entity.MemberEntity;
import com.example.board.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/me")
    public MemberDTO me(@AuthenticationPrincipal UserDetails user) {
        return memberService.findByEmail(user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto, HttpSession session) {

        MemberEntity member = memberService.login(dto.username(), dto.password());

        // 1. UserDetails 생성
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(member.getMemberEmail())
                .password(member.getMemberPassword())
                .authorities(member.getMemberRole())
                .build();

        // 2. Authentication 생성
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        // 3. SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. 세션에도 저장 (Security 유지용)
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
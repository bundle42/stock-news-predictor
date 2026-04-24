package com.example.board.member.controller;

import com.example.board.member.dto.LoginDTO;
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
import org.springframework.transaction.annotation.Transactional;
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

    @PutMapping("/me")
    @Transactional
    public ResponseEntity<String> updateMe(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody MemberDTO memberDTO
    ) {
        // 로그인 사용자 기준으로 강제 세팅
        memberDTO.setMemberEmail(user.getUsername());

        memberService.update(memberDTO);
        return ResponseEntity.ok("수정 성공");
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMe(@AuthenticationPrincipal UserDetails user) {
        memberService.deleteByEmail(user.getUsername());
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto, HttpSession session) {

        try {
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
        } catch (RuntimeException e) {
            if (e.getMessage().equals("EMAIL_NOT_FOUND")) {
                return ResponseEntity.status(404).body("EMAIL_NOT_FOUND");
            }

            if (e.getMessage().equals("INVALID_PASSWORD")) {
                return ResponseEntity.status(401).body("INVALID_PASSWORD");
            }

            return ResponseEntity.status(500).body("SERVER_ERROR");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberDTO memberDTO) {

        // 이메일 중복 체크
        if (memberService.emailExists(memberDTO.getMemberEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("EMAIL_EXISTS");
        }

        memberService.save(memberDTO);

        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
package com.example.board.member.controller;

import com.example.board.member.dto.LoginDto;
import com.example.board.member.dto.MemberDTO;
import com.example.board.member.entity.MemberEntity;
import com.example.board.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    // 생성자 주입
    private final MemberService memberService;

    // 회원가입 페이지 출력 요청
    @GetMapping("/save")
    public String saveForm() {
        return "member/save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        System.out.println("MemberController.save");
        System.out.println("memberDTO = " + memberDTO);
        memberService.save(memberDTO);
        return "member/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
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

        // 3. SecurityContext에 저장 (🔥 핵심)
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

    @GetMapping("/main")  // 고정 경로 먼저
    public String mainPage(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("userEmail", user.getUsername());
        return "member/main";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "member/detail";
    }

    @GetMapping("/update")
    public String updateForm(@AuthenticationPrincipal UserDetails user, Model model) {
        String email = user.getUsername();  // 현재 로그인 사용자 email
        MemberDTO memberDTO = memberService.updateForm(email);
        model.addAttribute("updateMember", memberDTO);
        return "member/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute MemberDTO memberDTO) {
        memberService.update(memberDTO);
        return "redirect:/member/" + memberDTO.getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        // 선택: 본인만 삭제 가능하도록 체크
        MemberDTO memberDTO = memberService.findById(id);
        if (!memberDTO.getMemberEmail().equals(user.getUsername())) {
            throw new RuntimeException("본인 계정만 삭제 가능");
        }
        memberService.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/email-check")
    public @ResponseBody String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        System.out.println("memberEmail = " + memberEmail);
        String checkResult = memberService.emailCheck(memberEmail);
        return checkResult;
    }

}

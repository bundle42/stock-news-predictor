package com.example.board.member.controller;

import com.example.board.member.dto.MemberDTO;
import com.example.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

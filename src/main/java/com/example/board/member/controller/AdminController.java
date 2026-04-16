package com.example.board.member.controller;

import com.example.board.member.dto.MemberDTO;
import com.example.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final MemberService memberService;

    @GetMapping("")
    public String adminMain() {
        return "admin/index";
    }

    @GetMapping("/members")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model사용
        model.addAttribute("memberList", memberDTOList);
        return "admin/members";
    }

    @GetMapping("/boards")
    public String boards() {
        return "admin/boards";
    }

    @GetMapping("/predict")
    public String predictPage() {
        return "admin/predict";
    }

    @GetMapping("/news")
    public String newsPage() {
        return "admin/news";
    }
}
package com.example.board.board.controller;

import com.example.board.board.dto.BoardDTO;
import com.example.board.board.dto.CommentDTO;
import com.example.board.board.service.BoardService;
import com.example.board.board.service.CommentService;
import com.example.board.board.service.NaverNewsService;
import com.example.board.member.dto.MemberDTO;
import com.example.board.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final NaverNewsService naverNewsService;
    private final MemberService memberService;

    // 글쓰기 페이지
    @GetMapping("/save")
    public String saveForm() {
        // Thymeleaf에서 #authentication.name 사용 → model에 이메일 안 넣어도 됨
        return "board/save";
    }

    // 글 저장
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO,
                       @AuthenticationPrincipal UserDetails user) throws IOException {

        String email = user.getUsername(); // 로그인 사용자 이메일
        boardService.save(boardDTO, email);
        return "redirect:/board/";
    }

    // 전체 게시글 목록
    @GetMapping("/")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "board/list";
    }

    // 게시글 상세
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id,
                           Model model,
                           @AuthenticationPrincipal UserDetails user) {

        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        List<CommentDTO> commentDTOList = commentService.findAll(id);

        // 로그인 사용자 이메일
        String email = user.getUsername();

        MemberDTO memberDTO = memberService.findByEmail(email);


        model.addAttribute("board", boardDTO);
        model.addAttribute("commentList", commentDTOList);

        model.addAttribute("sessionEmail", email);
        model.addAttribute("sessionMemberId", memberDTO.getId()); // 핵심

        return "board/detail";
    }

    // 글 수정 페이지
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {

        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "board/update";
    }

    // 글 수정 처리
    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO) {

        boardService.update(boardDTO);
        return "redirect:/board/" + boardDTO.getId();
    }

    // 글 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    // 내가 쓴 글만 보기
    @GetMapping("/myList")
    public String myList(@AuthenticationPrincipal UserDetails user, Model model) {

        String email = user.getUsername();
        List<BoardDTO> boardList = boardService.findByMemberEmail(email);

        model.addAttribute("boardList", boardList);
        return "board/myList";
    }

    // 네이버 뉴스 가져와서 저장
    @GetMapping("/news/import")
    public String importNews() {
        naverNewsService.saveNewsToBoard();
        return "redirect:/board/";
    }
}
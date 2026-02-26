package com.example.board.board.controller;

import com.example.board.board.dto.BoardDTO;
import com.example.board.board.dto.CommentDTO;
import com.example.board.board.service.BoardService;
import com.example.board.board.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/save")
    public String saveForm(HttpSession session) {

        if (session.getAttribute("loginId") == null) {
            return "redirect:/member/login";
        }

        return "board/save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO, HttpSession session) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        Long memberId = (Long) session.getAttribute("loginId");
        System.out.println("memberId = " + memberId);

        if (memberId == null) {
            return "redirect:/member/login";
        }

        boardService.save(boardDTO, memberId);
        return "redirect:/board/";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        // DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "board/list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable) {
        /*
            해당 게시글의 조회수를 하나 올리고
            게시글 데이터를 가져와서 detail.html에 출력
         */
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        /* 댓글 목록 가져오기 */
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList", commentDTOList);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "board/detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id,
                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                             Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        model.addAttribute("page", page);  // 수정 후 detail에서도 사용
        return "board/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO,
                         @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        BoardDTO board = boardService.update(boardDTO);
        // redirect할 때 page 정보 같이 전달
        return "redirect:/board/" + boardDTO.getId() + "?page=" + page;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<BoardDTO> boardList = boardService.paging(pageable);

        int blockLimit = 3;
        int currentPage = pageable.getPageNumber(); // 1-based 그대로 사용
        currentPage = currentPage + 1; // 1-based로 변환

        int startPage = (((int) Math.ceil((double) currentPage / blockLimit)) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("page", currentPage); // detail에서 돌아올 때 필요
        return "board/paging";
    }

}











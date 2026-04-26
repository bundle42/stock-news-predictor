package com.example.board.board.service;

import com.example.board.board.dto.BoardDTO;
import com.example.board.board.entity.BoardEntity;
import com.example.board.board.entity.BoardFileEntity;
import com.example.board.board.repository.BoardFileRepository;
import com.example.board.board.repository.BoardRepository;
import com.example.board.member.entity.MemberEntity;
import com.example.board.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

// DTO -> Entity (Entity Class)
// Entity -> DTO (DTO Class)

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final MemberRepository memberRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 일반 글 저장
    public void save(BoardDTO boardDTO, String email) throws IOException {
        MemberEntity member = memberRepository
                .findByMemberEmail(email)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        MultipartFile boardFile = boardDTO.getBoardFile();

        if (boardFile == null || boardFile.isEmpty()) {
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardEntity.setMember(member);
            boardRepository.save(boardEntity);
        } else {
            String originalFilename = boardFile.getOriginalFilename();
            String storedFileName = System.currentTimeMillis() + "_" + originalFilename;

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String savePath = uploadDir + File.separator + storedFileName;
            boardFile.transferTo(new File(savePath));

            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            boardEntity.setMember(member);

            BoardEntity board = boardRepository.save(boardEntity);

            BoardFileEntity boardFileEntity =
                    BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);

            boardFileRepository.save(boardFileEntity);
        }

    }

    @Transactional
    public List<BoardDTO> findAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return boardRepository.findAllByOrderByIdDesc(pageable)
                .stream()
                .map(BoardDTO::toBoardDTO)
                .toList();
    }

    @Transactional
    public BoardDTO findById(Long id) {
        boardRepository.updateHits(id);

        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        return BoardDTO.toBoardDTO(boardEntity);
    }

    @Transactional
    public void update(Long id, BoardDTO boardDTO, String email) {

        BoardEntity boardEntity = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        if (!boardEntity.getMember().getMemberEmail().equals(email)) {
            throw new AccessDeniedException("권한 없음");
        }

        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
    }

    @Transactional
    public void delete(Long boardId, String email) {

        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        if (!board.getMember().getMemberEmail().equals(email)) {
            throw new AccessDeniedException("권한 없음");
        }

        boardRepository.delete(board);
    }

    @Transactional
    public List<BoardDTO> findByMemberEmail(String email) {

        MemberEntity member = memberRepository.findByMemberEmail(email)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        return boardRepository.findAllByMemberId(member.getId())
                .stream()
                .map(BoardDTO::toBoardDTO)
                .toList();
    }

    // 네이버 뉴스 저장
    public void saveFromApi(BoardDTO boardDTO) {
        MemberEntity member = memberRepository.findById(boardDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardEntity.setMember(member);

        try {
            boardRepository.save(boardEntity);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("unique")) {
                System.out.println("중복 스킵");
            } else {
                throw e; // 진짜 에러는 터뜨림
            }
        }
    }

    @Transactional
    public void deleteDuplicateBoards() {
        List<Long> ids = boardRepository.findDuplicateIds();

        for (Long id : ids) {
            BoardEntity board = boardRepository.findById(id)
                    .orElseThrow();

            // DB 삭제 (cascade 작동)
            boardRepository.delete(board);
        }
    }
}

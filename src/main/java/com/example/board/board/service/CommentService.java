package com.example.board.board.service;

import com.example.board.board.dto.CommentDTO;
import com.example.board.board.entity.BoardEntity;
import com.example.board.board.entity.CommentEntity;
import com.example.board.board.repository.BoardRepository;
import com.example.board.board.repository.CommentRepository;
import com.example.board.member.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Long save(CommentDTO commentDTO, MemberEntity member) {
        BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        CommentEntity commentEntity =
                CommentEntity.toSaveEntity(commentDTO, boardEntity, member);

        return commentRepository.save(commentEntity).getId();
    }

    public void delete(Long id, Long loginMemberId) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getMember().getId().equals(loginMemberId)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        commentRepository.delete(comment);
    }

    public List<CommentDTO> findAll(Long boardId) {
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        //List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        List<CommentEntity> commentEntityList = commentRepository.findAllWithMember(boardEntity);
        /* EntityList -> DTOList */
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntityList) {
            CommentDTO dto = CommentDTO.toCommentDTO(commentEntity, boardId);
            commentDTOList.add(dto);
        }
        return commentDTOList;
    }

}

package com.example.board.board.dto;

import com.example.board.board.entity.CommentEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDTO {
    private Long id;
    private Long memberId;
    private String memberEmail;

    private String commentContents;
    private Long boardId;
    private LocalDateTime commentCreatedTime;

    public static CommentDTO toCommentDTO(CommentEntity commentEntity, Long boardId) {
        CommentDTO dto = new CommentDTO();

        dto.setId(commentEntity.getId());
        dto.setCommentContents(commentEntity.getCommentContents());
        dto.setCommentCreatedTime(commentEntity.getCreatedTime());
        dto.setBoardId(boardId);

        if (commentEntity.getMember() != null) {
            dto.setMemberId(commentEntity.getMember().getId());
            dto.setMemberEmail(commentEntity.getMember().getMemberEmail());
        }

        return dto;
    }
}

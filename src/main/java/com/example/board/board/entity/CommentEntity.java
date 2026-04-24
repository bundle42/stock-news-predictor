package com.example.board.board.entity;

import com.example.board.board.dto.CommentDTO;
import com.example.board.member.entity.MemberEntity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String commentContents;

    /* Board:Comment = 1:N */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    /* Member:Comment = 1:N */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    public static CommentEntity toSaveEntity(
            CommentDTO dto,
            BoardEntity board,
            MemberEntity member
    ) {
        CommentEntity entity = new CommentEntity();

        entity.setCommentContents(dto.getCommentContents());
        entity.setBoardEntity(board);
        entity.setMember(member);

        return entity;
    }
}

package com.example.board.board.repository;

import com.example.board.board.entity.BoardEntity;
import com.example.board.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // select * from comment_table where board_id=? order by id desc;
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);

    @Query("SELECT c FROM CommentEntity c JOIN FETCH c.member WHERE c.boardEntity = :board")
    List<CommentEntity> findAllWithMember(@Param("board") BoardEntity board);
}

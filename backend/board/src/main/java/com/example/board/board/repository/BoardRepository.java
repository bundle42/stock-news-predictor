package com.example.board.board.repository;

import com.example.board.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

// 자동으로 SQL문 만들어 주는 코드
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    // update board_table set board_hits=board_hits+1 where id=?
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id")
    void updateHits(@Param("id") Long id);

    List<BoardEntity> findAllByMemberId(Long memberId);

    List<BoardEntity> findByPubDateBetweenAndSearchQueryContainingOrderByPubDateAsc(String startDate, String endDate, String searchQuery);

    boolean existsByBoardTitle(String boardTitle);

    List<BoardEntity> findTop100ByOrderByIdDesc();
}















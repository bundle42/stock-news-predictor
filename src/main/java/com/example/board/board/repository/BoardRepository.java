package com.example.board.board.repository;

import com.example.board.board.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 자동으로 SQL문 만들어 주는 코드
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    // update board_table set board_hits=board_hits+1 where id=?
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id")
    void updateHits(@Param("id") Long id);

    List<BoardEntity> findByPubDateBetweenAndSearchQueryContainingOrderByPubDateAsc(String startDate, String endDate, String searchQuery);

    Page<BoardEntity> findAllByOrderByIdDesc(Pageable pageable);

    Page<BoardEntity> findByNewsLinkIsNullOrderByIdDesc(Pageable pageable);

    Page<BoardEntity> findAllByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);

    @Query("""
        SELECT b.id FROM BoardEntity b
        WHERE b.newsLink IS NOT NULL AND TRIM(b.newsLink) <> ''
        AND b.id NOT IN (
            SELECT MIN(b2.id)
            FROM BoardEntity b2
            WHERE b2.newsLink IS NOT NULL AND TRIM(b2.newsLink) <> ''
            GROUP BY b2.newsLink
        )
    """)
    List<Long> findDuplicateIds();

    @Query("SELECT b.newsLink FROM BoardEntity b WHERE b.newsLink IS NOT NULL")
    List<String> findAllLinks();
}

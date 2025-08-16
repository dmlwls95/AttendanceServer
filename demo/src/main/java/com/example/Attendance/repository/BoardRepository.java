package com.example.Attendance.repository;

import com.example.Attendance.entity.Board;
import com.example.Attendance.entity.BoardType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByBoardType(BoardType boardType, Pageable pageable);
    Optional<Board> findByIdAndBoardType(Long id, BoardType boardType);
    
    //추천수 +1
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Board b set b.recommendCount = b.recommendCount + 1 where b.id = :id")
    int incrementRecommend(@Param("id") Long id);

    //현재 추천 수 조회
    @Query("select b.recommendCount from Board b where b.id = :id")
    Integer getRecommendCount(@Param("id") Long id);
    
    List<Board> findTop10ByOrderByRecommendCountDesc();
}
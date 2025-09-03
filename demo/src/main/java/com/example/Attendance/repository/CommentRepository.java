package com.example.Attendance.repository;

import com.example.Attendance.entity.Comment;
import com.example.Attendance.entity.BoardType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 해당 게시판 타입의 게시글별 댓글 수 집계
    @Query("SELECT c.board.id, COUNT(c) FROM Comment c WHERE c.board.boardType = :boardType GROUP BY c.board.id")
    List<Object[]> countCommentsByBoardType(@Param("boardType") BoardType boardType);

    // 특정 게시글의 댓글 목록 조회 (필요시)
    @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId ORDER BY c.createdAt ASC")
    List<Comment> findCommentsByBoardId(@Param("boardId") Long boardId);
}

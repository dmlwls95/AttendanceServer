package com.example.Attendance.repository;

import com.example.Attendance.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Query("SELECT c FROM Comment c WHERE c.board.id = :boardId ORDER BY c.createdAt ASC")
	List<Comment> findCommentsByBoardId(@Param("boardId") Long boardId);
} 

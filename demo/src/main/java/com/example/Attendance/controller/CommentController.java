package com.example.Attendance.controller;

import com.example.Attendance.dto.CommentDTO;
import com.example.Attendance.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentDTO dto) {
        try {
            commentService.save(dto);
            return ResponseEntity.ok("댓글이 등록되었습니다.");
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 원인 출력
            return ResponseEntity.status(500).body("댓글 등록 실패: " + e.getMessage());
        }
    }

    // 특정 게시글에 대한 댓글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long boardId) {
        return ResponseEntity.ok(commentService.getComments(boardId));
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}
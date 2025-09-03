package com.example.Attendance.controller;

import com.example.Attendance.dto.CommentDTO;
import com.example.Attendance.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CommentDTO dto) {
        commentService.save(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<List<CommentDTO>> getComments(
            @PathVariable Long boardId,
            @RequestParam(required = false) String nickname) {
        List<CommentDTO> comments = commentService.getComments(boardId, nickname);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }
}

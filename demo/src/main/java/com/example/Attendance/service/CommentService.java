package com.example.Attendance.service;

import com.example.Attendance.dto.CommentDTO;
import com.example.Attendance.entity.Board;
import com.example.Attendance.entity.Comment;
import com.example.Attendance.repository.BoardRepository;
import com.example.Attendance.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public void save(CommentDTO dto) {
        log.info("save() 요청 → dto={}", dto);
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        Comment comment = Comment.builder()
                .writer(dto.getWriter())          // 꼭 필요합니다
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .board(board)
                .build();

        commentRepository.save(comment);
        log.info("댓글 저장 완료. id={}", comment.getId());
    }

    public List<CommentDTO> getComments(Long boardId, String currentUsername) {
        return commentRepository.findCommentsByBoardId(boardId)
                .stream()
                .map(comment -> {
                    boolean isOwner = currentUsername != null && currentUsername.equals(comment.getWriter());
                    Board board = comment.getBoard();
                    return CommentDTO.builder()
                            .id(comment.getId())
                            .writer(comment.getWriter())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .boardId(board != null ? board.getId() : null)
                            .boardType(board != null ? board.getBoardType().name() : null)
                            .isOwner(isOwner)  // 로그인 사용자와 작성자 일치 여부 반영
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}

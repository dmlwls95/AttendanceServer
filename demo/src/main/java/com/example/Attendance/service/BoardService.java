package com.example.Attendance.service;

import com.example.Attendance.dto.BoardDTO;
import com.example.Attendance.dto.BoardUpdateDTO;
import com.example.Attendance.entity.Board;
import com.example.Attendance.entity.BoardType;
import com.example.Attendance.repository.BoardRepository;
import com.example.Attendance.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository; // 댓글 리포지토리 추가
    private final NotificationService notificationService;

    public void write(BoardDTO dto) {
        Board entity = toEntity(dto);
        boardRepository.save(entity);

        if (entity.getBoardType() == BoardType.NOTICE) {
            try {
                notificationService.createNotification(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Page<BoardDTO> getListByType(String type, Pageable pageable) {
        BoardType boardType = BoardType.valueOf(type.toUpperCase());
        Page<Board> boards = boardRepository.findByBoardType(boardType, pageable);

        // 댓글 수 조회 (boardId, count)
        List<Object[]> commentCounts = commentRepository.countCommentsByBoardType(boardType);
        Map<Long, Long> commentCountMap = commentCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // DTO 변환하며 댓글수 세팅
        return boards.map(board -> {
            BoardDTO dto = toDTO(board);
            dto.setCommentCount(commentCountMap.getOrDefault(board.getId(), 0L).intValue());
            return dto;
        });
    }

    public BoardDTO getDetail(Long id) {
        return boardRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public void updateBoard(Long id, BoardUpdateDTO dto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다. id=" + id));
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
    }

    @Transactional
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    /**
     * 선택 삭제 메서드 (배치 삭제)
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 필요하다면 댓글도 삭제
        // commentRepository.deleteByBoardIdIn(ids); // 댓글 삭제 메서드가 있다면 호출

        // 배치 삭제
        boardRepository.deleteAllById(ids);
    }

    @Transactional
    public int recommend(Long id) {
        int updated = boardRepository.incrementRecommend(id);
        if (updated == 0) {
            throw new EntityNotFoundException("게시글이 존재하지 않습니다. id=" + id);
        }
        Integer cnt = boardRepository.getRecommendCount(id);
        return cnt != null ? cnt : 0;
    }

    private BoardDTO toDTO(Board board) {
        return BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .writeDate(board.getWriteDate())
                .boardType(board.getBoardType())
                .recommendCount(board.getRecommendCount())
                .commentCount(0) // 기본값 0, getListByType에서 실제값 세팅
                .build();
    }

    private Board toEntity(BoardDTO dto) {
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .writeDate(dto.getWriteDate() != null ? dto.getWriteDate() : LocalDateTime.now())
                .boardType(dto.getBoardType())
                .build();
    }

    public List<BoardDTO> getTop10Board() {
        List<Board> boardList = boardRepository.findTop10ByOrderByRecommendCountDesc();
        return boardList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

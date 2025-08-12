package com.example.Attendance.service;

import com.example.Attendance.dto.BoardDTO;
import com.example.Attendance.dto.BoardUpdateDTO;
import com.example.Attendance.entity.Board;
import com.example.Attendance.entity.BoardType;
import com.example.Attendance.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void write(BoardDTO dto) {
        Board entity = toEntity(dto);
        boardRepository.save(entity);
    }

    public Page<BoardDTO> getListByType(String type, Pageable pageable) {
        BoardType boardType = BoardType.valueOf(type.toUpperCase());
        return boardRepository.findByBoardType(boardType, pageable)
                              .map(this::toDTO);   // ✅ 공통 매핑 사용
    }

    public BoardDTO getDetail(Long id) {
        return boardRepository.findById(id)
                              .map(this::toDTO)    // ✅ 공통 매핑 사용
                              .orElse(null);
    }

    /* 게시글 수정 */
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

    /* 추천 +1 후 카운트 반환 */
    @Transactional
    public int recommend(Long id) {
        int updated = boardRepository.incrementRecommend(id);
        if (updated == 0) {
            throw new EntityNotFoundException("게시글이 존재하지 않습니다. id=" + id);
        }
        Integer cnt = boardRepository.getRecommendCount(id);
        return cnt != null ? cnt : 0;
    }

    /* ── 공통 매핑 ── */
    private BoardDTO toDTO(Board board) {
        return BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .writeDate(board.getWriteDate())
                .boardType(board.getBoardType())
                .recommendCount(board.getRecommendCount()) // 추천수 포함
                .build();
    }

    private Board toEntity(BoardDTO dto) {
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .writeDate(dto.getWriteDate() != null ? dto.getWriteDate() : LocalDateTime.now())
                .boardType(dto.getBoardType())   // DTO가 BoardType을 직접 들고있는 전제
                // recommendCount는 DB DEFAULT 0이면 생략
                .build();
    }
    
    /* ── 인기글 반환 ── */
    public List<BoardDTO> getTop10Board(){
    	List<Board> boardList = boardRepository.findTop10ByOrderByRecommendCountDesc();
    	return boardList.stream().map(board ->  toDTO(board)).collect(Collectors.toList());
    }
}
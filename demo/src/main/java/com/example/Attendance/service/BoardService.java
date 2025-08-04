package com.example.Attendance.service;

import com.example.Attendance.dto.BoardDTO;
import com.example.Attendance.entity.Board;
import com.example.Attendance.entity.BoardType;
import com.example.Attendance.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void write(BoardDTO dto) {
        Board entity = Board.builder()
            .title(dto.getTitle())
            .content(dto.getContent())
            .writer(dto.getWriter())
            .writeDate(dto.getWriteDate() != null ? dto.getWriteDate() : LocalDateTime.now())
            .boardType(dto.getBoardType()) // BoardType 직접 사용
            .build();
        boardRepository.save(entity);
    }

    public Page<BoardDTO> getListByType(String type, Pageable pageable) {
        BoardType boardType = BoardType.valueOf(type.toUpperCase());
        return boardRepository.findByBoardType(boardType, pageable)
            .map(board -> BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .writeDate(board.getWriteDate())
                .boardType(board.getBoardType()) // BoardType 그대로
                .build());
    }

    public BoardDTO getDetail(Long id) {
        return boardRepository.findById(id)
            .map(board -> BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .writeDate(board.getWriteDate())
                .boardType(board.getBoardType()) // BoardType 그대로
                .build())
            .orElse(null);
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }
}
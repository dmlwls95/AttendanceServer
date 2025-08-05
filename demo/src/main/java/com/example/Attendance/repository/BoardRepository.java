package com.example.Attendance.repository;

import com.example.Attendance.entity.Board;
import com.example.Attendance.entity.BoardType;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByBoardType(BoardType boardType, Pageable pageable);
    Optional<Board> findByIdAndBoardType(Long id, BoardType boardType);
}
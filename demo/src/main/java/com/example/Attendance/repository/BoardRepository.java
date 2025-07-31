package com.example.Attendance.repository;

import com.example.Attendance.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByWriter(String writer);
}
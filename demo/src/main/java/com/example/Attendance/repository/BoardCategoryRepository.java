package com.example.Attendance.repository;

import com.example.Attendance.entity.BoardCategory;
import com.example.Attendance.entity.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, BoardType> {
}

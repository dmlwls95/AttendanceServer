package com.example.Attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Attendance.entity.Department;
import com.example.Attendance.entity.WorkType;

public interface WorktypesRepository extends JpaRepository<WorkType, Long>{
	List<WorkType> findAll();
	WorkType findByWorktypename(String worktypename);
}

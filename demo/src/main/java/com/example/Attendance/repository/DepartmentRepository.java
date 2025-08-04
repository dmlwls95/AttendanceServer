package com.example.Attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Attendance.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long>{
	List<Department> findAll();
}

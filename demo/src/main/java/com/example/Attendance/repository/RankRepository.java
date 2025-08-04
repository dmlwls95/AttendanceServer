package com.example.Attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Attendance.entity.Department;
import com.example.Attendance.entity.Rank;

public interface RankRepository extends JpaRepository<Rank, Long>{
	List<Rank> findAll();

}

package com.example.Attendance.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Attendance.entity.Leave;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
	@Query("select count(l) from Leave l where :date between l.startDate and l.endDate")
	long countOnLeaveAt(@Param("date") LocalDate date);
}

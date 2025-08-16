package com.example.Attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Attendance.entity.Attendance;
import com.example.Attendance.entity.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{
	Optional<Attendance> findByUserAndDate(User user, LocalDate date);
	List<Attendance> findAllByUserAndDateBetween(User user, LocalDate from, LocalDate to);
	List<Attendance> findAllByDateBetween(LocalDate from, LocalDate to);
	
	long countByDateAndClockInIsNotNullAndClockOutIsNull(LocalDate date);
	long countByDateAndClockOutIsNotNull(LocalDate date);
	long countByDateAndIsLate(LocalDate date, int isLate);
	long countByDateAndIsLeftEarly(LocalDate date, int isLeftEarly);
	
	//boolean existsByUserAndDateAndClockInAtIsNotNull(User user, LocalDate date);
}
//이것처럼 board도 리펙토링
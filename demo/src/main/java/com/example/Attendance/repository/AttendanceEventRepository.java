package com.example.Attendance.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Attendance.entity.AttendanceEvent;
import com.example.Attendance.entity.User;


public interface AttendanceEventRepository extends JpaRepository<AttendanceEvent, Long> {
	//Page<AttendanceEvent> findByUser_Id(User user, Pageable pageable);
	List<AttendanceEvent> findByUser(User user, Pageable pageable);

}

package com.example.Attendance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Attendance.entity.User;
import com.example.Attendance.entity.UserMon;

public interface UsermonRepository extends JpaRepository<UserMon, Long>{
	Optional<UserMon> findByUser(User user);

}

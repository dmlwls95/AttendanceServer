package com.example.Attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Attendance.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);
	Optional<User>	findByEmpnum(String empnum);
	boolean existsByEmail(String email);
	boolean existsByEmpnum(String empnum);
	int deleteByEmpnum(String empnum);
}

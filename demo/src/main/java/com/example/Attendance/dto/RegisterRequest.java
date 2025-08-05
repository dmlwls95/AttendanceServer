package com.example.Attendance.dto;

import org.springframework.web.multipart.MultipartFile;

import com.example.Attendance.entity.User.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	private String empnum;
	private String email;
	private String work_name;
	private String password;
	private String role;
	private String dept;
	private String rank;
	private String worktype;
	private MultipartFile profileImage;
}

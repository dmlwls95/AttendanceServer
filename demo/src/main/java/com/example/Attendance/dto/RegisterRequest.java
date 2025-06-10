package com.example.Attendance.dto;

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
	private String email;
	private String name;
	private String password;
	private Role role;
}

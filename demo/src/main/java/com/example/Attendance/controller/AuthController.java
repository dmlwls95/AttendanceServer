package com.example.Attendance.controller;

import com.example.Attendance.dto.SignupRequest;
import com.example.Attendance.dto.UserResponse;
import com.example.Attendance.service.AuthService;
import com.example.Attendance.dto.LoginRequest;
import com.example.Attendance.dto.LoginResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@Operation(summary = "회원가입", description = "이름, 이메일, 비밀번호로 회원가입합니다.")
	@PostMapping("/signup")
	public UserResponse signup(@RequestBody SignupRequest request)
	{
		return authService.signup(request);
	}
	
	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 합니다.")
	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request)
	{
		return authService.login(request);
	}

}

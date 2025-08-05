package com.example.Attendance.controller;

import com.example.Attendance.service.AuthService;
import com.example.Attendance.dto.LoginRequest;
import com.example.Attendance.dto.LoginResponse;
import com.example.Attendance.dto.NavDataResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 합니다.")
	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request)
	{
		return authService.login(request);
	}
	
	@GetMapping("/navdata")
	public NavDataResponse getNavdata(Authentication auth)
	{
		String email = (String) auth.getPrincipal();
		return authService.getNavData(email);
	}

}

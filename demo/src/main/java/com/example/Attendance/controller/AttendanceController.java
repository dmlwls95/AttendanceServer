package com.example.Attendance.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.Attendance.dto.AttendanceMonthlyResponse;
import com.example.Attendance.dto.AttendanceResponse;
import com.example.Attendance.service.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	private final AttendanceService attendanceService;
	
	@Operation(summary = "출근기록")
	@PostMapping("/clock-in")
	public void clockIn(Authentication authentication)
	{
		String email = (String) authentication.getPrincipal();
		attendanceService.clockIn(email);
	}
	
	@Operation(summary = "퇴근기록")
	@PostMapping("/clock-out")
	public void clockOut(Authentication authentication)
	{
		String email = (String) authentication.getPrincipal();
		attendanceService.clockOut(email);
	}
	
	@Operation(summary = "오늘의 출퇴근 기록")
	@GetMapping("/today")
	public AttendanceResponse today(Authentication auth)
	{
		String email = (String) auth.getPrincipal();
		return attendanceService.getTodayAttendance(email);
	}
	
	@Operation(summary = "기간별 근무 요약")
	@GetMapping("/summary")
	public List<AttendanceResponse> summary(
			@RequestParam("from") String from, 
			@RequestParam("to") String to, 
			Authentication auth)
	{
		String email = (String) auth.getPrincipal();
		return attendanceService.getSummary(email, LocalDate.parse(from), LocalDate.parse(to));
	}
	
	@Operation(summary = "월간 근무 통계 조회")
	@GetMapping("/summary/monthly")
	public AttendanceMonthlyResponse summaryMonth(
			@RequestParam int year,
			@RequestParam int month,
			Authentication auth)
	{
		String email = (String) auth.getPrincipal();
		return attendanceService.getMonthlySummary(email, year, month);
	}
	

}

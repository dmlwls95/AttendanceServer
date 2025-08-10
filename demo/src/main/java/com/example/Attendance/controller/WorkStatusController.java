package com.example.Attendance.controller;

import com.example.Attendance.dto.AttendStatusDTO;
import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.service.WorkStatusService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.sql.Date; //Mybatis/Oracle 파라미터 전달에 안전함
import java.time.LocalDate;
import java.util.List;



@RestController
@RequestMapping("/api/work")
@CrossOrigin(origins = "*") //리액트에서 호출하기위해 임시 허용
public class WorkStatusController {
	
	private final WorkStatusService service;
	
	public WorkStatusController(WorkStatusService service) {
		this.service = service;
	}

// 주간 근무 요약
@GetMapping("/weekly/summary")
public List<WorkSummaryDTO> getWeeklySummary(
		@RequestParam String userid,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end){
	
	return service.getWeeklyWorkSummary(userid, Date.valueOf(start), Date.valueOf(end));
	}

// 주간 출결 상태
@GetMapping("/weekly/attendance")
public List<AttendStatusDTO> getWeeklyAttendance(
		@RequestParam String userid,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end){
	
	return service.getWeeklyAttendStatus(userid, Date.valueOf(start), Date.valueOf(end));
	}
}

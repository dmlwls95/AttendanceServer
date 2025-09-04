package com.example.Attendance.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.Attendance.dto.AttendanceEventResponse;
import com.example.Attendance.dto.AttendanceHistoryResponse;
import com.example.Attendance.dto.AttendanceMonthlyResponse;
import com.example.Attendance.dto.AttendanceResponse;
import com.example.Attendance.dto.NormalResponse;
import com.example.Attendance.dto.WeeklyKpiResponse;
import com.example.Attendance.entity.AttendanceEvent;
import com.example.Attendance.service.AttendanceService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

// import 주간 월간 분석 DTO 추가
import com.example.Attendance.dto.WeeklyDashboardResponse;
import com.example.Attendance.dto.MonthlyDashboardResponse;
// imports
import java.nio.charset.StandardCharsets;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.Attendance.entity.User;
import com.example.Attendance.repository.UserRepository;


@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
	private final AttendanceService attendanceService;
	//csv
	   private final UserRepository userRepository;
	//
	
	@GetMapping("/hascheckin")
	public boolean hasCheckIn(Authentication authentication)
	{
		String email = (String) authentication.getPrincipal();
		return attendanceService.hasCheckedInToday(email);
	}
	
	@Operation(summary = "출근 하기")
	@PostMapping("/clock-in")
	public void clockIn(Authentication authentication)
	{
		String email = (String) authentication.getPrincipal();
		attendanceService.clockIn(email);
	}
	
	@Operation(summary = "퇴근 하기")
	@PostMapping("/clock-out")
	public void clockOut(Authentication authentication)
	{
		String email = (String) authentication.getPrincipal();
		attendanceService.clockOut(email);
	}
	
	@GetMapping("/hasbreakout")
	public boolean hasBreakOut(Authentication authentication)
	{
		String email = (String) authentication.getPrincipal();
		return attendanceService.hasBreakOut(email);
	}
	
	@Operation(summary = "외출 시작")
	@PostMapping("/outingstart")
	public NormalResponse outingStart(Authentication authentication)
	{
		String email = (String) authentication.getPrincipal();
		return attendanceService.StartOuting(email);
	}
	
	@Operation(summary = "외출 끝")
	@PostMapping("/outingend")
	public NormalResponse outingEnd(Authentication authentication)
	{
		String email = (String) authentication.getPrincipal();
		return attendanceService.EndOuting(email);
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
	public AttendanceHistoryResponse summary(
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
	
	@GetMapping("/recent")
	public List<AttendanceEventResponse> getRecentAttendance(
				@RequestParam int howmany,
				Authentication auth
			)
	{
		String email = (String) auth.getPrincipal();
		return attendanceService.getRecentAttendance(email, howmany);
	}
	
	//-----------------------------------------------------------------------------------------
	
	
	// 유저 주간 근무 정보
	@Operation(summary = "주간 근무 통계 조회")
	@GetMapping("/dashboard/weekly")
	public WeeklyDashboardResponse dashboardWeekly(
	        @RequestParam String date,
	        Authentication auth
	) {
		String email = (String) auth.getPrincipal();
		
		return attendanceService.getWeeklyDashboard(email, LocalDate.parse(date));
	}
	
	// 유저 월간 근무 정보
	// ─────────────────────────────────────────────────────────────
	// 월간 대시보드(도넛/막대/카드 상단용)
	// GET /attendance/dashboard/monthly?year=YYYY&month=M
	// ─────────────────────────────────────────────────────────────
	@GetMapping("/dashboard/monthly")
	public MonthlyDashboardResponse dashboardMonthly(
	        @RequestParam int year,
	        @RequestParam int month,
	        Authentication auth
	) {
	    String email = (String) auth.getPrincipal();
	    return attendanceService.getMonthlyDashboard(email, year, month);
	}

	// ─────────────────────────────────────────────────────────────
	// 주간 KPI(하단 카드의 "주간 지각/잔업/총시간") - 월요일 ~ 오늘
	// GET /attendance/kpi?from=YYYY-MM-DD&to=YYYY-MM-DD
	// ─────────────────────────────────────────────────────────────
	@GetMapping("/kpi")
	public WeeklyKpiResponse weeklyKpi(
	        @RequestParam String from,
	        @RequestParam String to,
	        Authentication auth
	) {
	    String email = (String) auth.getPrincipal();
	    return attendanceService.getWeeklyKpi(email, LocalDate.parse(from), LocalDate.parse(to));
	}
	
	//csv
	@GetMapping(value = "/export", produces = "text/csv")
	public ResponseEntity<byte[]> exportCsvForMe(
	        Authentication authentication,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
	) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ResponseEntity.status(401).build();
	    }

	    // 프로젝트가 (String) principal 구조이므로 그대로 사용
	    String email = (String) authentication.getPrincipal();

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

	    byte[] csv = attendanceService.exportCsv(user, start, end);
	    if (csv == null) csv = new byte[0];

	    // 파일명 안전 처리
	    String filename = String.format("attendance_%s_%s_%s.csv",
	            email.replaceAll("[\\\\/:*?\"<>|]", "_"), start, end);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
	    headers.setContentDisposition(
	            ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build()
	    );

	    return ResponseEntity.ok().headers(headers).body(csv);
	}
}

package com.example.Attendance.controller;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Attendance.dto.AdminAttendanceSummaryResponse;
import com.example.Attendance.dto.AdminHomepageChartDataResponse;
import com.example.Attendance.dto.AllUsersResponse;
import com.example.Attendance.dto.AttendanceResponse;
import com.example.Attendance.dto.AttendanceUpdateRequest;
import com.example.Attendance.dto.RegisterFormInfoRequest;
import com.example.Attendance.dto.RegisterRequest;
import com.example.Attendance.dto.RegisterResponse;
import com.example.Attendance.dto.UserResponse;
import com.example.Attendance.dto.UserdataResponse;
import com.example.Attendance.dto.WorkingRowDTO;
import com.example.Attendance.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	private final AdminService adminService;
	private final Logger log = LoggerFactory.getLogger(getClass());
	

	
	@Operation(summary = "모든 유저의 이메일 및 이름 조회")
	@GetMapping("getallusers")
	public List<AllUsersResponse> getAllUsersEmailAndName()
	{
		return adminService.getAllUsers();
	}
	
	@Operation(summary = "기간별 모든 출퇴근 조회")
	@GetMapping("attendance/history")
	public List<AttendanceResponse> getAllAttendance(
			@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
	)
	{
		log.debug(from.toString());
		return adminService.getAttendanceRecords(from, to);
	}
	
	@GetMapping("attendance/monthly-summary")
	public AdminAttendanceSummaryResponse getMonthlyAttendanceSummary(
			@RequestParam int year,
			@RequestParam int month)
	{
		return adminService.getAttendanceMonthSummary(year, month);
	}
	
	@Operation(summary = "특정 기록 수정")
	@PutMapping("attendance/history")
	public ResponseEntity<?> putAttendance(@RequestBody AttendanceUpdateRequest request)
	{
		return adminService.putUserAttendanceRecord(request);
	}
	
	
	//Deprecated
	@GetMapping("attendance/user")
	public List<AttendanceResponse> getUserAttendance(
				Authentication auth,
				@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
				@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
			)
	{
		String email = (String) auth.getPrincipal();
		return adminService.getUserAttendanceRecord(email, from, to);
	}
	
	@Operation(summary = "특정 유저의 기간별 모든 출퇴근 조회")
	@GetMapping("attendance/byemail")
	public List<AttendanceResponse> getUserAttendanceByEmail(
				String email,
				@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
				@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
			)
	{
		return adminService.getUserAttendanceRecord(email, from, to);
	}
	
	@GetMapping("attendance/export")
	public ResponseEntity<byte[]> exportAttendanceCsv(
			@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
			)
	{
		String csv = adminService.generateCsvForMonth(from, to);
		byte[] output = csv.getBytes(StandardCharsets.UTF_8);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
		headers.setContentDisposition(
				ContentDisposition.attachment().filename("근무기록_"+ from + "_" + to, StandardCharsets.UTF_8).build()
				);
		
		return new ResponseEntity<>(output, headers, HttpStatus.OK);
	}
	
	@GetMapping("attendance/exportbyemail")
	public ResponseEntity<byte[]> exportAttendanceCsvByEmail(
			String email,
			@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
			)
	{
		String csv = adminService.generateCsvByEmail(email, from, to);
		byte[] output = csv.getBytes(StandardCharsets.UTF_8);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
		headers.setContentDisposition(
				ContentDisposition.attachment().filename("근무기록_"+ from + "_" + to, StandardCharsets.UTF_8).build()
				);
		
		return new ResponseEntity<>(output, headers, HttpStatus.OK);
	}
	
	
	@GetMapping("usermanagement/forminfo")
	public RegisterFormInfoRequest getRegisterFormInfo()
	{
		return adminService.getFormRegisterInfo();
	}
	
	@PostMapping("usermanagement/signup")
	public RegisterResponse RegisterUser(
				@ModelAttribute RegisterRequest request
			)
	{
		return adminService.RegisterUser(request);
	}
	
	@GetMapping("usermanagement/checkuser")
	public UserdataResponse getUserdata(
				@RequestParam String empno
			)
	{
		return adminService.findAndGetUserData(empno);
	}
	
	@PutMapping("usermanagement/userupdate")
	public RegisterResponse UpdateUser(
				@ModelAttribute RegisterRequest request
			) {
		return adminService.UpdateUser(request);
	}
	
	/*
	 * @DeleteMapping("usermanagement/userdelete") public RegisterResponse
	 * DeleteUser(
	 * 
	 * @RequestParam String empnum ) { return
	 * adminService.DeleteUserByempno(empnum); }
	 */
	
	@DeleteMapping("usermanagement/userdelete")
	public RegisterResponse DeleteUser(@RequestParam String empnum)
	{
		return adminService.DeleteUserByempno(empnum);
	}
//	public RegisterResponse DeleteUser(@RequestParam String empnum) {
//	    return adminService.softDeleteUserByEmpnum(empnum); 
//	}
	
	@GetMapping("usermanagement/userlist")
	public Page<UserdataResponse> list(
				@RequestParam(defaultValue = "0") int page,
				@RequestParam(defaultValue = "10") int size,
				@RequestParam(defaultValue = "hiredate") String sortBy,
				@RequestParam(defaultValue = "desc") String direction
			){
		return adminService.getUsersPagination(page, size, sortBy, direction);
	}
	
	@GetMapping("usermanagement/findattendance")
	public AttendanceResponse findAttendance(
				@RequestParam String empnum,
				@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate adate
			) {
		return adminService.findAttendanceByDate(empnum, adate);
	}
	
	
	//*******홈페이지
	@GetMapping("today-summarychart")
	public AdminHomepageChartDataResponse getTodaySummaryChart()
	{
		return adminService.getTodaySummary();
	}
	//근무자 리스트
	@GetMapping("attendance/working-list")
    @Operation(summary = "오늘(또는 지정일) 근무자 리스트 (상태 포함)")
    public Page<WorkingRowDTO> workingList(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status,   // PRESENT | LEFT | ABSENT | LEAVE
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "empnum") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return adminService.getWorkingList(date, status, page, size, sortBy, direction);
    }


}

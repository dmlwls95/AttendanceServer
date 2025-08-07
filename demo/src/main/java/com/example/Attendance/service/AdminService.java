package com.example.Attendance.service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Attendance.Util.LocaldateParser;
import com.example.Attendance.dto.AdminAttendanceSummaryResponse;
import com.example.Attendance.dto.AllUsersResponse;
import com.example.Attendance.dto.AttendanceResponse;
import com.example.Attendance.dto.AttendanceUpdateRequest;
import com.example.Attendance.dto.RegisterFormInfoRequest;
import com.example.Attendance.dto.RegisterRequest;
import com.example.Attendance.dto.RegisterResponse;
import com.example.Attendance.dto.UserResponse;
import com.example.Attendance.dto.UserdataResponse;
import com.example.Attendance.entity.Attendance;
import com.example.Attendance.entity.Department;
import com.example.Attendance.entity.Rank;
import com.example.Attendance.entity.User;
import com.example.Attendance.entity.User.Role;
import com.example.Attendance.entity.WorkType;
import com.example.Attendance.repository.*;
import com.mysql.cj.log.Log;

import ch.qos.logback.classic.pattern.DateConverter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	
	private final AttendanceRepository attendanceRepository;
	private final UserRepository userRepository;
	private final DepartmentRepository departmentRepository;
	private final WorktypesRepository worktypeRepository;
	private final RankRepository rankRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final int startOfWork = 9;
	private final int endOfWork = 18;
	private static final Logger log = LoggerFactory.getLogger(AdminService.class);

	
	public List<AllUsersResponse> getAllUsers()
	{
		List<AllUsersResponse> records =  userRepository.findAll().stream().map( usr -> AllUsersResponse.builder()
					.name(usr.getName())
					.email(usr.getEmail())
					.build()
				).collect(Collectors.toList());

		return records;
	}
	
	public List<AttendanceResponse> getAttendanceRecords(LocalDate from, LocalDate to)
	{
		List<Attendance> records = attendanceRepository.findAllByDateBetween(from, to);
		
		return records.stream().map(a -> AttendanceResponse.builder()
				.id(a.getId())
				.name(a.getUser().getName())
				.email(a.getUser().getEmail())
				.date(a.getDate())
				.clockIn(a.getClockIn())
				.clockOut(a.getClockOut())
				.isLate(a.getIsLate())
				.isLeftEarly(a.getIsLeftEarly())
				.totalHours(a.getTotalHours())
				.build()
				).collect(Collectors.toList());
	}
	
	
	public List<AdminAttendanceSummaryResponse> getAttendanceMonthSummary(int year, int month)
	{
		LocalDate fromDate = LocalDate.of(year, month, 1);
		LocalDate toDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
		
		List<User> users = userRepository.findAll();
		List<AdminAttendanceSummaryResponse> result = new ArrayList<>();
		
		int getWorkableDays = AttendanceService.getWorkableDays(year, month);
		
		if(users.size() == 1)
		{
			return result;
		}
		
		for(User user : users)
		{
			List<Attendance> records = attendanceRepository.findAllByUserAndDateBetween(user, fromDate, toDate);
			
			int totalDaysWorked = 0;
			double totalHours = 0.0;
						
			for(Attendance a : records)
			{
				if(a.getClockIn() != null)
				{
					totalDaysWorked++;
				}
				
				if(a.getClockOut() != null && a.getClockIn() != null && a.getTotalHours() != null)
				{
					totalHours += a.getTotalHours();
				}
			}
			int missedDays = Math.max(0, getWorkableDays - totalDaysWorked);
			double averageHours = totalDaysWorked > 0 ? totalHours / totalDaysWorked : 0.0;
			
			result.add(AdminAttendanceSummaryResponse.builder()
					.userId(user.getId())
					.email(user.getEmail())
					.totalDaysWorked(totalDaysWorked)
					.totalHours(totalHours)
					.averageHours(averageHours)
					.missedDays(missedDays)
					.build()
					);
		}
		return result;
	}
	
	public List<AttendanceResponse> getUserAttendanceRecord(String email, LocalDate from, LocalDate to)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		List<Attendance> records = attendanceRepository.findAllByUserAndDateBetween(user, from, to);
		List<AttendanceResponse> result = new ArrayList<>();
		
		for(Attendance record : records)
		{
			result.add(AttendanceResponse.builder()
					.id(record.getId())
					.email(user.getEmail())
					.name(user.getName())
					.date(record.getDate())
					.clockIn(record.getClockIn())
					.clockOut(record.getClockOut())
					.isLate(record.getIsLate())
					.isLeftEarly(record.getIsLeftEarly())
					.totalHours(record.getTotalHours())
					.build()
					);
		}
		return result;
	}
	
	public ResponseEntity<?> putUserAttendanceRecord(AttendanceUpdateRequest request)
	{
		Attendance attendance = attendanceRepository.findById(request.getId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id 액세스 에러"));
		
		attendance.setDate(request.getDate());
		attendance.setClockIn(request.getClockIn());
		attendance.setClockOut(request.getClockOut());
		attendance.setIsLate(request.getIsLate());
		attendance.setIsLeftEarly(request.getIsLeftEarly());
		
		// 총 근무시간 재계산
		double hours = (double) java.time.Duration.between(
				request.getClockIn().getHour() < startOfWork ? LocalDateTime.of(request.getDate().getYear(), request.getDate().getMonth(), request.getDate().getDayOfMonth(), startOfWork, 0, 0) : request.getClockIn()
				, request.getClockOut().getHour() > endOfWork ? LocalDateTime.of(request.getDate().getYear(), request.getDate().getMonth(), request.getDate().getDayOfMonth(), endOfWork, 0, 0) : request.getClockOut() ).toMinutes() / 60;
	    attendance.setTotalHours(hours);
	    
	    attendanceRepository.save(attendance);
	    
	    Map<String, String> response = new HashMap<>();
	    response.put("message", "수정 완료");
	    return ResponseEntity.ok(response);
	}
	
	public String generateCsvForMonth(LocalDate from, LocalDate to)
	{ 

		
		List<Attendance> records = attendanceRepository.findAllByDateBetween(from, to);
		
		StringBuilder result = new StringBuilder("\uFEFF");
		result.append("날짜,이메일,출근시간,퇴근시간,지각,조퇴,총근무시간\n");
		
		
		for(Attendance record : records)
		{
			String clockInStr = record.getClockIn() != null ?
					record.getClockIn().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
					: "";
			String clockOutStr = record.getClockIn() != null ?
					record.getClockOut().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
					: "";
			String totalHoursStr = record.getTotalHours() != null ? record.getTotalHours().toString() : "";
			result.append(String.join(",", record.getDate().toString() , record.getUser().getEmail() , clockInStr, clockOutStr,record.getIsLate().toString(),record.getIsLeftEarly().toString(), totalHoursStr) + "\n") ;
		}
		
		return result.toString();
	}
	
	
	public RegisterFormInfoRequest getFormRegisterInfo()
	{
		List<WorkType> workdata = worktypeRepository.findAll();
		List<Department> deptdata = departmentRepository.findAll();
		List<Rank> rankdata = rankRepository.findAll();
		List<String> workstr = new ArrayList<String>();
		List<String> deptstr = new ArrayList<String>();
		List<String> rankstr = new ArrayList<String>();
		
		workdata.forEach(data -> {
			workstr.add(data.getWorktypename());
		});
		deptdata.forEach(data -> {
			deptstr.add(data.getDeptname());
		});
		rankdata.forEach(data -> {
			rankstr.add(data.getRankname());
		});
		
		
		
		RegisterFormInfoRequest info = RegisterFormInfoRequest.builder()
				.depts(deptstr)
				.ranks(rankstr)
				.worktypes(workstr)
				.build();
		
		return info;
		
	}
	
	public RegisterResponse RegisterUser(RegisterRequest request)
	{
		if(userRepository.existsByEmail(request.getEmail()))
		{
			return RegisterResponse.builder()
					.success(false)
					.message("이미 존재하는 이메일 입니다.")
					.build();
		}
		
		MultipartFile file = request.getProfileImage();
		String filename = request.getEmpnum() + request.getWork_name() + ".png";
		try {
			
			String uploadDir = System.getProperty("user.dir") + "/src/uploads/profileimages/";
			File destination = new File(uploadDir + filename);
			file.transferTo(destination);
			
		} catch (IOException e) {
			e.printStackTrace();
			return RegisterResponse.builder()
					.success(false)
					.message("프로필 사진에 문제가 있습니다.")
					.build();

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		Role role = null;
		if(request.getRole().toUpperCase().equals("ADMIN")) {
			role = Role.ADMIN;
		}else {
			role = Role.USER;
		}
		
		LocalTime start = request.getWorkstarttime() == null ? null : LocalTime.parse(request.getWorkstarttime());
		LocalTime end = request.getWorkendtime() == null ? null : LocalTime.parse(request.getWorkendtime());
		
		
		User user = User.builder()
				.empnum(request.getEmpnum())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.name(request.getWork_name())
				.role(role)
				.worktype(worktypeRepository.findByWorktypename(request.getWorktype()))
				.dept(departmentRepository.findByDeptname(request.getDept()))
				.rank(rankRepository.findByRankname(request.getRank()))
				.profileImageUrl(filename)
				.workStartTime(start)
				.workEndTime(end)
				.hiredate(LocaldateParser.parseToLocalDate(request.getHiredate()))
				.build();
		userRepository.save(user);
		
		
		
		
		
		return RegisterResponse.builder()
				.success(true)
				.message("성공적으로 회원가입을 완료했습니다.")
				.build();
	}
	
	public RegisterResponse UpdateUser(RegisterRequest request)
	{
		if(!userRepository.existsByEmail(request.getEmail()))
		{
			return RegisterResponse.builder()
					.success(false)
					.message("존재 하지 않는 사원입니다.")
					.build();
		}
		
		MultipartFile file = request.getProfileImage();
		String filename = request.getEmpnum() + request.getWork_name() + ".png";
		try {
			
			String uploadDir = System.getProperty("user.dir") + "/src/uploads/profileimages/";
			File destination = new File(uploadDir + filename);
			file.transferTo(destination);
			
		} catch (IOException e) {
			e.printStackTrace();
			return RegisterResponse.builder()
					.success(false)
					.message("프로필 사진에 문제가 있습니다.")
					.build();

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		Role role = null;
		if(request.getRole().toUpperCase().equals("ADMIN")) {
			role = Role.ADMIN;
		}else {
			role = Role.USER;
		}
		
		LocalTime start = request.getWorkstarttime() == null ? null : LocalTime.parse(request.getWorkstarttime());
		LocalTime end = request.getWorkendtime() == null ? null : LocalTime.parse(request.getWorkendtime());
		
		/*User user = User.builder()
				.empnum(request.getEmpnum())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.name(request.getWork_name())
				.role(role)
				.worktype(worktypeRepository.findByWorktypename(request.getWorktype()))
				.dept(departmentRepository.findByDeptname(request.getDept()))
				.rank(rankRepository.findByRankname(request.getRank()))
				.profileImageUrl(filename)
				.workStartTime(start)
				.workEndTime(end)
				.build();
		*/
		User user = userRepository.findByEmpnum(request.getEmpnum())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원 입니다."));
		
		user.setEmpnum(request.getEmpnum());
		user.setName(request.getWork_name());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(role);
		user.setWorktype(worktypeRepository.findByWorktypename(request.getWorktype()));
		user.setDept(departmentRepository.findByDeptname(request.getDept()));
		user.setRank(rankRepository.findByRankname(request.getRank()));
		user.setProfileImageUrl(filename);
		user.setWorkStartTime(start);
		user.setWorkEndTime(end);
		user.setHiredate(LocaldateParser.parseToLocalDate(request.getHiredate()));
		
		userRepository.save(user);
		
		return RegisterResponse.builder()
				.success(true)
				.message("성공적으로 업데이트를 완료했습니다.")
				.build();
	}
	
	public UserdataResponse findAndGetUserData(String empno)
	{
		if(!userRepository.existsByEmpnum(empno))
		{
			throw new IllegalArgumentException("존재하지 않는 사원 번호 입니다.");
		}
		
		User usr = userRepository.findByEmpnum(empno).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원 번호 입니다."));
		
		
		return UserdataResponse.builder()
				.empnum(empno)
				.name(usr.getName())
				.email(usr.getEmail())
				.role(usr.getRole().toString())
				.rank(usr.getRank().getRankname())
				.worktype(usr.getWorktype().getWorktypename())
				.depttype(usr.getDept().getDeptname())
				.profileImageUrl(usr.getProfileImageUrl())
				.hiredate(usr.getHiredate())
				.workStartTime(usr.getWorkStartTime())
				.workEndTime(usr.getWorkEndTime())
				.build();
	}
	
	
	

}

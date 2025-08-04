package com.example.Attendance.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

import com.example.Attendance.dto.AdminAttendanceSummaryResponse;
import com.example.Attendance.dto.AllUsersResponse;
import com.example.Attendance.dto.AttendanceResponse;
import com.example.Attendance.dto.AttendanceUpdateRequest;
import com.example.Attendance.dto.RegisterFormInfoRequest;
import com.example.Attendance.dto.RegisterRequest;
import com.example.Attendance.dto.UserResponse;
import com.example.Attendance.entity.Attendance;
import com.example.Attendance.entity.Department;
import com.example.Attendance.entity.Rank;
import com.example.Attendance.entity.User;
import com.example.Attendance.entity.WorkType;
import com.example.Attendance.repository.AttendanceRepository;
import com.example.Attendance.repository.DepartmentRepository;
import com.example.Attendance.repository.RankRepository;
import com.example.Attendance.repository.UserRepository;
import com.example.Attendance.repository.WorktypesRepository;
import com.mysql.cj.log.Log;

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
	
	public UserResponse register(RegisterRequest request)
	{
		if(userRepository.existsByEmail(request.getEmail()))
		{
			throw new IllegalArgumentException("이미 존재하는 이메일 입니다");
		}
		
		User user = User.builder()
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.name(request.getName())
				.role(request.getRole())
				.build();
		userRepository.save(user);
		return UserResponse.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
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
	

}

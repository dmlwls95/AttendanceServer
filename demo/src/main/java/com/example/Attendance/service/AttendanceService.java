package com.example.Attendance.service;

import com.example.Attendance.controller.*;
import com.example.Attendance.dto.AttendanceEventResponse;
import com.example.Attendance.dto.AttendanceHistoryResponse;
import com.example.Attendance.dto.AttendanceMonthlyResponse;
import com.example.Attendance.dto.AttendanceResponse;
import com.example.Attendance.dto.NormalResponse;
import com.example.Attendance.dto.UserAttendanceHistory;
import com.example.Attendance.entity.Attendance;
import com.example.Attendance.entity.AttendanceEvent;
import com.example.Attendance.entity.User;
import com.example.Attendance.repository.AttendanceEventRepository;
import com.example.Attendance.repository.AttendanceRepository;
import com.example.Attendance.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class AttendanceService {

    //private final AttendanceController attendanceController;
	
	private final AttendanceRepository attendanceRepository;
	private final AttendanceEventRepository attendanceEventRepository;
	private final UserRepository userRepository;
	private final SimpMessagingTemplate messaging;
	
	//private final int startOfWork = 9;
	//private final int endOfWork = 18;

	
	public void clockIn(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		LocalDate today = LocalDate.now();

		
		Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
				.orElse(null);
		
		if(attendance != null && attendance.getClockIn() != null)
		{
			throw new IllegalStateException("이미 출근 했습니다");
		}
		
		if(attendance == null)
		{
			attendance = Attendance.builder()
					.user(user)
					.date(today)
					.build();
		}
		
		attendance.setClockIn(LocalDateTime.now());
		
		
		
		// 지각 판별
		if(attendance.getClockIn() != null)
		{
			LocalDateTime actual = attendance.getClockIn().truncatedTo(ChronoUnit.MINUTES);
			LocalDateTime due = LocalDateTime.of(attendance.getDate(), user.getWorkStartTime()).truncatedTo(ChronoUnit.MINUTES);
			boolean isLate = actual.isAfter(due);
			attendance.setIsLate(isLate ? 1 : 0);
			//LocalTime standardStart = LocalTime.of(user.getWorkStartTime(), 0);
			//attendance.setIsLate(attendance.getClockIn().toLocalTime().isAfter(standardStart)? 1 : 0);
		}
		
		attendanceRepository.save(attendance);
		
		// stomp 신호 전송
		messaging.convertAndSendToUser(user.getEmail(), "/queue/attendance", new AttendanceSignal("CHECKED_IN", LocalDateTime.now()));
	}
	
	public void clockOut(String email) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

	    LocalDate today = LocalDate.now();

	    Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
	            .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다"));

	    if (attendance.getClockIn() == null) {
	        throw new IllegalStateException("출근 기록이 없어 퇴근을 기록할 수 없습니다.");
	    }

	    // --- 사용자 근무 시간 (LocalTime) ---
	    LocalTime startTime = user.getWorkStartTime();
	    LocalTime endTime   = user.getWorkEndTime();
	    if (startTime == null || endTime == null) {
	        // 필요 시 안전 기본값 (원래 상수 쓰던 값). 둘 다 채워져 있다면 이 블록은 삭제해도 됨.
	        startTime = startTime != null ? startTime : LocalTime.of(9, 0);
	        endTime   = endTime   != null ? endTime   : LocalTime.of(18, 0);
	    }

	    // --- 퇴근 시간 저장 (분 단위 절삭) ---
	    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
	    attendance.setClockOut(now);

	    // --- 근무일 경계 계산 (야간근무 고려: 종료가 시작 이전이면 다음 날 종료) ---
	    LocalDate     workDate   = attendance.getDate();
	    LocalDate     endDate    = endTime.isBefore(startTime) ? workDate.plusDays(1) : workDate;
	    LocalDateTime shiftStart = LocalDateTime.of(workDate, startTime).truncatedTo(ChronoUnit.MINUTES);
	    LocalDateTime shiftEnd   = LocalDateTime.of(endDate,   endTime  ).truncatedTo(ChronoUnit.MINUTES);

	    // --- 실제 출퇴근 시각(분 단위) ---
	    LocalDateTime clockIn  = attendance.getClockIn().truncatedTo(ChronoUnit.MINUTES);
	    LocalDateTime clockOut = now;

	    // --- 근무시간 계산: 회사 경계 내로 클램프 ---
	    LocalDateTime from = clockIn.isBefore(shiftStart) ? shiftStart : clockIn; // max(clockIn, shiftStart)
	    LocalDateTime to   = clockOut.isAfter(shiftEnd)   ? shiftEnd   : clockOut; // min(clockOut, shiftEnd)
	    
	    // --- 잔업시간 추가
	    long overtimeMin = 0;
	    if (clockOut.isAfter(shiftEnd)) {
	        overtimeMin = Duration.between(shiftEnd, clockOut).toMinutes(); // shiftEnd 초과분만
	    }
	    attendance.setOvertimeMinutes((int)overtimeMin);

	    long minutes = 0;
	    if (!to.isBefore(from)) {
	        minutes = Duration.between(from, to).toMinutes();
	    }
	    attendance.setTotalHours(minutes / 60.0);

	    // --- 조퇴 판별(분 단위): 종료 시각 이전에 나가면 조퇴, 같은 분이면 아님 ---
	    boolean leftEarly = clockOut.isBefore(shiftEnd);
	    attendance.setIsLeftEarly(leftEarly ? 1 : 0);

	    attendanceRepository.save(attendance);
	}
	
	public NormalResponse StartOuting(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

	    LocalDate today = LocalDate.now();

	    Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
	            .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다"));

	    if (attendance.getClockIn() == null) {
	        throw new IllegalStateException("출근 기록이 없어 외출을 기록할 수 없습니다.");
	    }
	    
	    if(attendance.getOutStart() != null)
	    {
	    	return NormalResponse.builder()
	    			.success(false)
	    			.message("이미 외출한 상태에선 외출할 수 없습니다")
	    			.build();
	    }
	    
	    attendance.setOutStart(LocalDateTime.now());
	    
	    attendanceRepository.save(attendance);
	 // stomp 신호 전송
 		messaging.convertAndSendToUser(user.getEmail(), "/queue/attendance", new AttendanceSignal("BREAK_OUT", LocalDateTime.now()));
	    return NormalResponse.builder()
    			.success(true)
    			.message("외출 시작")
    			.build();
	    
	}
	
	public NormalResponse EndOuting(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

	    LocalDate today = LocalDate.now();

	    Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
	            .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다"));

	    if (attendance.getClockIn() == null) {
	        throw new IllegalStateException("출근 기록이 없어 외출을 기록할 수 없습니다.");
	    }
	    
	    if(attendance.getOutStart() == null)
	    {
	    	return NormalResponse.builder()
	    			.success(false)
	    			.message("외출하지 않으면 복귀 할 수 없습니다.")
	    			.build();
	    }
	    
	    attendance.setOutEnd(LocalDateTime.now());
	    
	    attendanceRepository.save(attendance);
	 // stomp 신호 전송
 		messaging.convertAndSendToUser(user.getEmail(), "/queue/attendance", new AttendanceSignal("BREAK_IN", LocalDateTime.now()));
	    return NormalResponse.builder()
    			.success(true)
    			.message("외출 끝")
    			.build();
	    
	}
	
	public AttendanceResponse getTodayAttendance(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		LocalDate today = LocalDate.now();
		
		Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
				.orElseThrow(() -> new IllegalStateException("오늘 출근 기록이 없습니다."));
		
		return AttendanceResponse.builder()
				.date(attendance.getDate())
				.clockIn(attendance.getClockIn())
				.clockOut(attendance.getClockOut())
				.isLate(attendance.getIsLate())
				.isLeftEarly(attendance.getIsLeftEarly())
				.totalHours(attendance.getTotalHours())
				.build();
	}
	
	
	int workdays = 0;
	int worktimes = 0;
	int overtimes = 0;
	int absencedays = 0;
	public AttendanceHistoryResponse getSummary(String email, LocalDate from, LocalDate to)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));
		List<Attendance> list = attendanceRepository.findAllByUserAndDateBetween(user, from, to);
		List<UserAttendanceHistory> dtos = list.stream()
			    .<UserAttendanceHistory>map(a -> UserAttendanceHistory.builder()
			        .workdate(a.getDate())
			        .clock_in(a.getClockIn())
			        .clock_out(a.getClockOut())
			        .workMinute(a.getTotalHours() == null ? 0 : (int)Math.round(a.getTotalHours() * 60))
			        .overtimeMinute(a.getOvertimeMinutes())
			        .build()
			    )
			    .collect(Collectors.toList());
		
		
		
		
		
		dtos.forEach(val -> {
			if(val.getWorkMinute() >0) {
				workdays++;
			}else {
				absencedays++;
			}
			
			worktimes += val.getWorkMinute();
			overtimes += val.getOvertimeMinute();
			
		});
		
		
		return AttendanceHistoryResponse.builder()
				.workDays(workdays)
				.workTimes(worktimes)
				.overTimes(overtimes)
				.absenceDays(absencedays)
				.historyList(dtos)
				.build();
		
	}
	
	public AttendanceMonthlyResponse getMonthlySummary(String email, int year, int month)
	{
		int nowYear = Year.now().getValue();
		
		if(year > nowYear)
		{
			throw new IllegalArgumentException("요청한 연도는 현재 연도보다 클 수 없습니다");
		}
		if(month < 1 || month >12)
		{
			throw new IllegalArgumentException("월(month)는 1~12사이의 값이여야 합니다.");
		}
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		LocalDate fromDate = LocalDate.of(year, month, 1);
		LocalDate toDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
		
		
		List<Attendance> records = attendanceRepository.findAllByUserAndDateBetween(user, fromDate, toDate);
		
		
		int totalDaysWorked = 0;
		double totalHours = 0;
		int totalWorkableDays = getWorkableDays(year, month);
		
		
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
		
		double averageHours = totalDaysWorked > 0 ? totalHours / totalDaysWorked : 0.0;
		int missedDays = Math.max(0, totalWorkableDays - totalDaysWorked);
		
		return AttendanceMonthlyResponse.builder()
				.totalDaysWorked(totalDaysWorked)
				.totalHours(totalHours)
				.averageHours(averageHours)
				.missedDays(missedDays)
				.build();
	}
	
	public static int getWorkableDays(int year, int month)
	{
		return (int)IntStream.rangeClosed(1, YearMonth.of(year, month).lengthOfMonth())
				.mapToObj(day -> LocalDate.of(year, month, day))
				.filter(d -> !(d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY))
				.count();
	}
	
	public boolean hasCheckedInToday(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		LocalDate today = LocalDate.now();

		Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
				.orElse(null);
		
		if(attendance != null && attendance.getClockIn() != null)
		{
			return true;
		}
		return false;
	}
	
	public boolean hasBreakOut(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		LocalDate today = LocalDate.now();

		Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
				.orElse(null);
		
		if(attendance != null && attendance.getOutStart() != null)
		{
			return true;
		}
		return false;
	}
	
	
	public List<AttendanceEventResponse> getRecentAttendance(String email ,int howmany)
	{
		User user = userRepository.findByEmail(email)
		.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원 입니다"));
		Pageable pageable = PageRequest.of(0, howmany, Sort.by(Sort.Direction.DESC, "occurredAt"));
		
		
		return attendanceEventRepository.findByUser(user, pageable).stream().map(AttendanceEventResponse::from).collect(Collectors.toList());
	}
	
	public static class AttendanceSignal{
		private final String type;
		private final LocalDateTime at;
		
		public AttendanceSignal(String type, LocalDateTime at) {
			this.type = type;
			this.at = at;
			
		}
		public String getType() {return type;}
		public LocalDateTime getAt() {return at;}
	}
	
	
	
	

	

}

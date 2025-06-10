package com.example.Attendance.service;

import com.example.Attendance.controller.*;
import com.example.Attendance.dto.AttendanceMonthlyResponse;
import com.example.Attendance.dto.AttendanceResponse;
import com.example.Attendance.entity.Attendance;
import com.example.Attendance.entity.User;
import com.example.Attendance.repository.AttendanceRepository;
import com.example.Attendance.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class AttendanceService {

    //private final AttendanceController attendanceController;
	
	private final AttendanceRepository attendanceRepository;
	private final UserRepository userRepository;
	
	private final int startOfWork = 9;
	private final int endOfWork = 18;

	
	public void clockIn(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		LocalDate today = LocalDate.now();

		/*Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
				.orElse(Attendance.builder()
					.user(user)
					.date(today)
					.clockIn(LocalDateTime.now())
					.build());*/
		
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
			LocalTime standardStart = LocalTime.of(startOfWork, 0);
			attendance.setIsLate(attendance.getClockIn().toLocalTime().isAfter(standardStart)? 1 : 0);
		}
		
		attendanceRepository.save(attendance);
	}
	
	public void clockOut(String email)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		
		LocalDate today = LocalDate.now();
		
		Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
				.orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다"));
		
		
		/*if(attendance.getClockOut() != null)
		{
			throw new IllegalArgumentException("이미 퇴근 했습니다");
		}*/
		
		attendance.setClockOut(LocalDateTime.now());
		
		double hours = (double) java.time.Duration.between(
				attendance.getClockIn(), attendance.getClockOut()).toMinutes() / 60;
		
		attendance.setTotalHours(hours);
		
		// 조퇴 판별
		if(attendance.getClockOut() != null)
		{
			LocalTime standardEnd = LocalTime.of(endOfWork, 0);
			
			attendance.setIsLeftEarly(attendance.getClockOut().toLocalTime().isBefore(standardEnd) ? 1 : 0);
		}
		
		attendanceRepository.save(attendance);
				
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
	
	public List<AttendanceResponse> getSummary(String email, LocalDate from, LocalDate to)
	{
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));
		List<Attendance> list = attendanceRepository.findAllByUserAndDateBetween(user, from, to);
		
		return list.stream().map( a-> AttendanceResponse.builder()
				.date(a.getDate())
				.clockIn(a.getClockIn())
				.clockOut(a.getClockOut())
				.isLate(a.getIsLate())
				.isLeftEarly(a.getIsLeftEarly())
				.totalHours(a.getTotalHours())
				.build()
				).collect(Collectors.toList());
		
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

}

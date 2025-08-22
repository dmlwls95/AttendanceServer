package com.example.Attendance.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyDashboardResponse {

	// 월요일 ~ 일요일까지의 정보
	private List<DayOfWeekResponse> info;
	
	// 총 근무 시간
	private long totalWorktime;
	
	// 총 잔업 시간
	private long totalOvertime;
		
	// 이번주 남은 근무 시간 (총 근무 시간 - 현재 근무 시간)
	private long leftTime;
	
	// 총 시간 (총 근무시간 + 총 잔업 시간)
	private long totalTime;

}


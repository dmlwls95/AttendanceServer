package com.example.Attendance.dto;

import lombok.*;
import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayOfWeekResponse {

	//월요일 ~ 일요일
	private String dayOfweek;
	
	// 요일에 해당하는 날짜
	private LocalDate date;
	
	// 일별 근무 시간
	private long workTime;
	
	// 일별 잔업 시간
	private long overTime;
	
	// Enum 값으로 status 표시
	private StatusType status;
	
	private DayType dayType;
	
	// 정상, 지각, 결근, 조퇴, 지각 + 조퇴, 휴일(주말)
	public enum StatusType{
		NORMAL, LATE, ABSENCE, LEFTEARLY, LATEANDLEFTEARLY, DEFAULT
	}
	
	public enum DayType{
		WEEKDAY, WEEKEND
	}
	
	private LocalTime clockIn;
	private LocalTime clockOut;
}

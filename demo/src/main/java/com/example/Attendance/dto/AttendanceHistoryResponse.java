package com.example.Attendance.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//유저 페이지 기록 조회 dto
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceHistoryResponse {
	private int workDays;
	private int workTimes;
	private int overTimes;
	private int absenceDays;
	private List<UserAttendanceHistory> historyList;
}

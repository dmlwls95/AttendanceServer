package com.example.Attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAttendanceHistory {
	private LocalDate workdate;
	private LocalDateTime clock_in;
	private LocalDateTime clock_out;
	private int workMinute;
	private int overtimeMinute;
}

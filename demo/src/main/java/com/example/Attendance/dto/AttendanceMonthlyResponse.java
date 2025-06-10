package com.example.Attendance.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceMonthlyResponse {
	private int totalDaysWorked;
	private double totalHours;
	private double averageHours;
	private int missedDays;
}

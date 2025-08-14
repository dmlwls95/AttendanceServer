package com.example.Attendance.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.lang.Nullable;

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
public class AdminHomepageChartDataResponse {
	private LocalDate date;
	private int totalEmployees;
	private int present;
	private int left;
	private int leave;
	private int absent;
	private int lateArrivals;
	private int earlyLeaves;
	private Instant generatedAt;
}

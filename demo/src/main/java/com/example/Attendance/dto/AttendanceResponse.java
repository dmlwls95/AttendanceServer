package com.example.Attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.lang.Nullable;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
	private Long id;
	@Nullable
	private String email;
	@Nullable
	private String name;
	private LocalDate date;
	private LocalDateTime clockIn;
	private LocalDateTime clockOut;
	private int isLate;
	private int isLeftEarly;
	private Double totalHours;

}

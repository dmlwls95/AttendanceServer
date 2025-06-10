package com.example.Attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceUpdateRequest {
	private Long id;
	private LocalDate date;
	private LocalDateTime clockIn;
	private LocalDateTime clockOut;
	private int isLate;
	private int isLeftEarly;
}

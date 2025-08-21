package com.example.Attendance.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WorkingRowDTO {
	private Long userId;
	private String empnum;
	private String name;
	private String deptName;
	private LocalDateTime clockIn;
	private LocalDateTime clockOut;
	private String status; // PRESENT | LEFT | ABSENT | LEAVE
}

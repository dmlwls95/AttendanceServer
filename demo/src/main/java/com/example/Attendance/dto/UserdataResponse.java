package com.example.Attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserdataResponse {
	private String empnum;
	private String name;
	private String email;
	private String role;
	private String rank;
	private String worktype;
	private String depttype;
	private String profileImageUrl;
	private LocalDate hiredate;
	private LocalTime workStartTime;
	private LocalTime workEndTime;
}

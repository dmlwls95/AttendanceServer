package com.example.Attendance.dto;


import java.util.List;

import com.example.Attendance.entity.Department;
import com.example.Attendance.entity.WorkType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterFormInfoRequest {
	private List<String> depts;
	private List<String> ranks;
	private List<String> worktypes;

}

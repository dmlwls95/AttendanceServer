package com.example.Attendance.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalResponse {
	private boolean success; //true
	private String message;
}

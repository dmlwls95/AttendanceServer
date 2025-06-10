package com.example.Attendance.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllUsersResponse {
	private String name;
	private String email;
}

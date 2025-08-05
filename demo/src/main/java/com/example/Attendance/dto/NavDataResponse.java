package com.example.Attendance.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NavDataResponse {
	private String name;
	private String empno;
	private String rank;
	private String profileUrl;
}

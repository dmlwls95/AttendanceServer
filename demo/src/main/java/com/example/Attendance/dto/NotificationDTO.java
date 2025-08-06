package com.example.Attendance.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
	private String title;											// board 의 title	[제목]
	private String writeDate;									// board의 writeDate [작성일]
	private String message = "새로운 공지 업데이트";		// 알람 메세지 ["새로운 공지 업데이트"]
}

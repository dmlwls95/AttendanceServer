package com.example.Attendance.dto;

import com.example.Attendance.entity.NotificationStatus.ReadType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
	private Long id;
	private Long boardid;
	private String title;											// board 의 title	[제목]
	private String writeDate;									// board의 writeDate [작성일]
	@Builder.Default
	private String message = "새로운 공지 업데이트";		// 알람 메세지 ["새로운 공지 업데이트"]
	private String writer;
	private ReadType isRead;
}

package com.example.Attendance.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import com.example.Attendance.entity.AttendanceEvent;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceEventResponse {
	private AttendanceEvent.EventType eventType;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime occurredAt;
	
	private AttendanceEvent.SourceType source;
	private String ipOrDevice;
	private String userAgent;
	private String note;
	private String correlationId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime createdAt;
	
	public static AttendanceEventResponse from(AttendanceEvent e) {
		return AttendanceEventResponse.builder()
				.eventType(e.getEventType())
				.occurredAt(e.getOccurredAt())
				.source(e.getSource())
				.ipOrDevice(e.getIpOrDevice())
				.userAgent(e.getUserAgent())
				.note(e.getNote())
				.correlationId(e.getCorrelationId())
				.createdAt(e.getCreatedAt())
				.build();
	}
	

}

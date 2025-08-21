package com.example.Attendance.entity;

import javax.persistence.*;

import lombok.*;

@Entity

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "notistatus")
public class NotificationStatus {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notistatus_seq")
	@SequenceGenerator(name="notistatus_seq", sequenceName="NOTISTATUS_SEQ", allocationSize = 1)
	private Long statusId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_id")
	private Notification notification;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User users;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "is_read")
	private ReadType isRead = ReadType.NOTREAD;
	
	public enum ReadType{
		NOTREAD, READ
	}
}

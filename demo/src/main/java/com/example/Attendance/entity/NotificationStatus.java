package com.example.Attendance.entity;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // 명시적으로 지정
    @OnDelete(action = OnDeleteAction.CASCADE)
	private User users;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "is_read")
	private ReadType isRead = ReadType.NOTREAD;
	
	public enum ReadType{
		NOTREAD, READ
	}
}

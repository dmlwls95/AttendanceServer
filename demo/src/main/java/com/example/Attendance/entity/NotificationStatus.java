package com.example.Attendance.entity;


import javax.persistence.*;

import lombok.*;

@Entity
@NoArgsConstructor

@Getter
@Setter

@Table(name = "notistatus")
public class NotificationStatus {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notistatus_seq")
	@SequenceGenerator(name="notistatus_seq", sequenceName="NOTISTATUS_SEQ", allocationSize = 1)
	private Integer statusId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_id")
	private Notification notification;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User users;
	
	@Column(name = "is_read")
	private Integer isRead = 0;
}

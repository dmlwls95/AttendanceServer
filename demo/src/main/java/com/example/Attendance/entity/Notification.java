package com.example.Attendance.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@NoArgsConstructor

@Getter
@Setter

@Table(name = "notification")
public class Notification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
	@SequenceGenerator(name="notification_seq", sequenceName="NOTIFICATION_SEQ", allocationSize = 1)
	private int notiId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;
	
}

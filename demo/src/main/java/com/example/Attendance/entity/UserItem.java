package com.example.Attendance.entity;

import java.time.LocalDateTime;

import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "useritem")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="useritem_seq")
	@SequenceGenerator(name = "useritem_seq", sequenceName = "USERITEM_SEQ", allocationSize = 1)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@OneToOne
	@JoinColumn(name = "itemmaster_id")
	private ItemMaster itemmaster_id;
	private Integer quantity;
	private Integer equipped;
	private LocalDateTime acquired_at;
}

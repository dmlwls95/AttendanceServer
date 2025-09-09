package com.example.Attendance.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // 명시적으로 지정
    @OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	@OneToOne
	@JoinColumn(name = "itemmaster_id")
	private ItemMaster itemmaster_id;
	private Integer quantity;
	private Integer equipped;
	private LocalDateTime acquired_at;
}

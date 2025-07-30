package com.example.Attendance.entity;

import javax.persistence.*;
import lombok.*;



@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="user_seq")
	@SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
	private Long id;//primary
	
	@Column(nullable = false, name = "work_name")
	private String name;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	public enum Role {
		USER, ADMIN
	}
}

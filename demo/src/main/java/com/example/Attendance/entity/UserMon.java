package com.example.Attendance.entity;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "usermon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMon {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usermon_seq")
	@SequenceGenerator(name = "usermon_seq", sequenceName = "USERMON_SEQ", allocationSize = 1)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private String mon_name;
	
	@Column(name = "mon_level")
	private Integer level;
	
	private Integer evolve_stage;
	
	private String emotion;
	
	private Integer gummy;
	
	private Integer point;

}

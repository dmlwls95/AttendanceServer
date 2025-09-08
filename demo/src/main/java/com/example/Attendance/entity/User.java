package com.example.Attendance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import javax.persistence.*;

import com.example.Attendance.Util.LocalTimeStringConverter;

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
	private Long id;
	
	@Column(unique = true, nullable=false)
	private String empnum;
	
	@Column(nullable = false, name = "work_name")
	private String name;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	// 프로그램 내부에서만 관리
	@Enumerated(EnumType.STRING)
	private Role role;
	
	public enum Role {
		USER, ADMIN
	}
	
	public enum Delete {
		NOTDELETE, DELETE
	}
	
	@Enumerated(EnumType.STRING) 
    @Column(name = "is_deleted", nullable = false)
    private User.Delete isDeleted = User.Delete.NOTDELETE;
	
	@ManyToOne
	@JoinColumn(name= "rankid")
	private Rank rank;
	
	
	@ManyToOne
	@JoinColumn(name= "worktypeid")
	private WorkType worktype;
	
	@ManyToOne
	@JoinColumn(name= "deptid")
	private Department dept;
	
	@Column(name="profileimageurl")
	private String profileImageUrl;
	
	@Column(name = "hiredate")
    private LocalDate hiredate;
	
	//Entity 자체는 Localtime이지만 db엔 09:00 같은 값으로 저장됌
	@Column(name = "work_start_time", length = 5)
    @Convert(converter = LocalTimeStringConverter.class)
    private LocalTime workStartTime;

    @Column(name = "work_end_time", length = 5)
    @Convert(converter = LocalTimeStringConverter.class)
    private LocalTime workEndTime;



}

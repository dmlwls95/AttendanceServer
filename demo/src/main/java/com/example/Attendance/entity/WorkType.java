package com.example.Attendance.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "WORKTYPES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkType {
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "worktype_seq")
    @SequenceGenerator(name = "worktype_seq", sequenceName = "WORKTYPE_SEQ", allocationSize = 1)
    private Long worktypeid;
	
	private String worktypename;
}

package com.example.Attendance.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ranks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rank {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rank_seq")
    @SequenceGenerator(name = "rank_seq", sequenceName = "RANK_SEQ", allocationSize = 1)
    private Long rankid;
	
	
	private String rankname;
	
}

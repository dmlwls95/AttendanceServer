package com.example.Attendance.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvolutionData {
	private int evolveStage;
	private int requiredGummy;
	private int requiredPoint;
	private String monName;
}

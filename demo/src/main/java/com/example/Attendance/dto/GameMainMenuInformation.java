package com.example.Attendance.dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameMainMenuInformation {
	private String condition;
	private String monname;
	private int evolveStage;
	private int monlevel;
	private int worktime;
	private int totalGummy;
	private int totalPoint;
}

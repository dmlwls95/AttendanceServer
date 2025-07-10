package com.example.Attendance.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameAttendanceResultDto {
	private boolean success;
    private String message;
    private int rewardedGummy;
    private String errKey;
}

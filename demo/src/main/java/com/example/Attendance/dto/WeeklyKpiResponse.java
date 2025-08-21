package com.example.Attendance.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WeeklyKpiResponse {
    // 접속 주(월~접속일) 기준 KPI (분)
    private int lateMinutes;     // 주간 지각 누적(분)
    private int overMinutes;     // 주간 잔업(범위 외) 누적(분)
    private int totalMinutes;    // 주간 총 근무(분)
}

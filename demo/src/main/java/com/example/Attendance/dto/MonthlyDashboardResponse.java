package com.example.Attendance.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MonthlyDashboardResponse {
    // ─ 상단 도넛용 ─
    private int presentDays;    // 출근일수
    private int lateDays;       // 지각일수
    private int absentDays;     // 결근일수
    private int holidayDays;    // (임시) 주말근무일수

    // ─ 중간 막대용(분) ─
    private int normalMinutes;    // 소정근로분
    private int overtimeMinutes;  // 잔업분
    private int holidayMinutes;   // 휴일근로분(토/일 근무분)
    private int nightMinutes;     // 야간근로분(22:00~05:00)

    // ─ 참고 ─
    private int workableDays;     // 평일수
}

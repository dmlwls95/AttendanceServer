package com.example.Attendance.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MonthlyDashboardResponse {
    // ── 도넛(일 수)
    private int presentDays;     // 출근일(평일+주말 출근 포함)
    private int lateDays;        // 지각일(ATTENDANCE.is_late==1)
    private int absentDays;      // 결근일(평일에 출근 기록 없음)
    private int holidayDays;     // 휴일(토/일에 출근 안한 날 수)

    // ── 막대(분 단위)
    private int normalMinutes;   // 소정 근로(평일, 근무시간 범위 내)
    private int overtimeMinutes; // 범위 외 근로(평일, 근무시간 前/後 포함)
    private int holidayMinutes;  // 주말(토/일) 근무 전체
    private int nightMinutes;    // 22:00~익일 05:00 구간 근로

    // ── 추가 지표
    private int workableDays;    // 해당월 평일(월~금) 수
    private int lateMinutes;     // 월간 지각 누적(분)
    private int totalMinutes;    // 월간 총 근무(분) = normal+overtime+holiday (night은 중복구간)
}

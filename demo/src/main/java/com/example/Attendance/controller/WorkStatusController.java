package com.example.Attendance.controller;

import com.example.Attendance.dto.AttendStatusDTO;
import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.service.WorkStatusService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
public class WorkStatusController {

    private final WorkStatusService service;

    @Operation(summary = "주간 근무 요약 조회 (근무/연장/총근무 시간 포함)")
    @GetMapping("/weekly/summary")
    public List<WorkSummaryDTO> getWeeklySummary(
            @RequestParam String userid,
            @RequestParam String start, // yyyy-MM-dd
            @RequestParam String end    // yyyy-MM-dd
    ) {
        return service.getWeeklyWorkSummary(userid, start, end);
    }

    @Operation(summary = "주간 출근 상태 조회 (정상/지각/결근/휴일 등)")
    @GetMapping("/weekly/attendance")
    public List<AttendStatusDTO> getWeeklyAttendance(
            @RequestParam String userid,
            @RequestParam String start, // yyyy-MM-dd
            @RequestParam String end    // yyyy-MM-dd
    ) {
        return service.getWeeklyAttendStatus(userid, start, end);
    }
}

package com.example.Attendance.controller;

import com.example.Attendance.dto.AttendStatusDTO;
import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.service.WorkStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work")
@CrossOrigin(
    origins = { "http://localhost:5173", "http://localhost:3000" },
    allowCredentials = "true"
)
@RequiredArgsConstructor
public class WorkStatusController {

    private final WorkStatusService service;

    /** 주간 근태 요약 (근무/연장/총근무) */
    @GetMapping("/weekly/summary")
    public List<WorkSummaryDTO> getWeeklySummary(
            @RequestParam String userid,
            @RequestParam String start,   // "YYYY-MM-DD"
            @RequestParam String end      // "YYYY-MM-DD"
    ) {
        return service.getWeeklyWorkSummary(userid, start, end);
    }

    /** 주간 출근 상태 (정상/지각/결근/휴일 + 출근시각) */
    @GetMapping("/weekly/attendance")
    public List<AttendStatusDTO> getWeeklyAttendance(
            @RequestParam String userid,
            @RequestParam String start,   // "YYYY-MM-DD"
            @RequestParam String end      // "YYYY-MM-DD"
    ) {
        return service.getWeeklyAttendStatus(userid, start, end);
    }
}
package com.example.Attendance.controller;

import com.example.Attendance.dto.AttendStatusDTO;
import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.service.WorkStatusService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date; //Mybatis/Oracle 파라미터 전달에 안전함
import java.time.LocalDate;
import java.util.List;



@RestController
@RequestMapping("/api/work")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class WorkStatusController {

    private final WorkStatusService service;

    public WorkStatusController(WorkStatusService service) {
        this.service = service;
    }

    @GetMapping("/weekly/summary")
    public List<WorkSummaryDTO> getWeeklySummary(@RequestParam String userid,
                                                 @RequestParam String start,
                                                 @RequestParam String end) {
        return service.getWeeklyWorkSummary(userid, start, end);
    }

    @GetMapping("/weekly/attendance")
    public List<AttendStatusDTO> getWeeklyAttendance(@RequestParam String userid,
                                                     @RequestParam String start,
                                                     @RequestParam String end) {
        return service.getWeeklyAttendStatus(userid, start, end);
    }
}
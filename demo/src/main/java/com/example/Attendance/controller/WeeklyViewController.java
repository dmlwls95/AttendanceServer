package com.example.Attendance.controller;

import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.service.WorkStatusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller //JSP 화면(View) 반환용 컨트롤러임을 의미 (@RestController 아님!)
public class WeeklyViewController {

    private final WorkStatusService service;
   
    // 생성자를 통해 서비스 주입 (스프링이 자동으로 WorkStatusService 구현체를 넣어줌)
    public WeeklyViewController(WorkStatusService service) {
        this.service = service;
    }
    
    // 주간 근태 화면 페이지 요청 처리 메서드
    // 브라우저에서 아래 URL로 접속 시 이 메서드가 실행됨
    // URL: http://localhost:9090/work/weekly?userid=joyget&start=2025-08-01&end=2025-08-07
    
    @GetMapping("/work/weekly") // GET 요청 매핑 (URL: /work/weekly)
    public String weeklyPage(@RequestParam String userid,
                             @RequestParam String start,
                             @RequestParam String end,
                             Model model) {

        List<WorkSummaryDTO> list = service.getWeeklyWorkSummary(userid, start, end);
        model.addAttribute("weeklySummary", list);
        model.addAttribute("userid", userid);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        return "weekly/summary"; // /WEB-INF/views/weekly/summary.jsp
    }
}
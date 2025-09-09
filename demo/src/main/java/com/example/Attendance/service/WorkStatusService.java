//package com.example.Attendance.service;
//
//import com.example.Attendance.dto.WorkSummaryDTO;
//import com.example.Attendance.dto.AttendStatusDTO;
//import com.example.Attendance.repository.WorkStatusMapper;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.List;
//
//@Service
//public class WorkStatusService {
//
//	private final WorkStatusMapper workStatusMapper;
//	
//	public WorkStatusService(WorkStatusMapper workStatusMapper) {
//		this.workStatusMapper = workStatusMapper;
//	}
//	
//	public List<WorkSummaryDTO> getWeeklyWorkSummary(String userid, String start, String end) {
//		return workStatusMapper.getWeeklyWorkSummary(userid, start, end);
//	}
//	
//	public List<AttendStatusDTO> getWeeklyAttendStatus(String userid, String start, String end){
//		return workStatusMapper.getWeeklyAttendStatus(userid, start, end);
//	}
//}

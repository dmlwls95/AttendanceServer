package com.example.Attendance.repository;

import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.dto.AttendStatusDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface WorkStatusMapper {

	List<WorkSummaryDTO> getWeeklyWorkSummary(@Param("userid") String userid,
											 @Param("start") Date start,
											 @Param("end") Date end);
	
	List<AttendStatusDTO> getWeeklyAttendStatus(@Param("userid")String userid,
											   @Param("start") Date start,
											   @Param("end") Date end);
}

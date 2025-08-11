package com.example.Attendance.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.dto.AttendStatusDTO;

@Mapper
public interface WorkStatusMapper {

    @Select({
        "SELECT",
        "  userid,",
        "  workdate,",
        "  dayofweek,",
        "  workinghours,",
        "  extendhours,",
        "  totalhours,",
        "  workstatus,",
        "  arrivaltime",
        "FROM WEEKLY_SUMMARY",
        "WHERE userid = #{userid}",
        "  AND workdate BETWEEN TO_DATE(#{start}, 'YYYY-MM-DD') AND TO_DATE(#{end}, 'YYYY-MM-DD')"
    })
    List<WorkSummaryDTO> getWeeklyWorkSummary(
        @Param("userid") String userid,
        @Param("start") String start,   // 예: 2025-08-01
        @Param("end")   String end      // 예: 2025-08-07
    );

    @Select({
        "SELECT",
        "  userid,",
        "  workdate,",
        "  attendstatus,",
        "  arrivaltime",
        "FROM WEEKLY_ATTENDSTATUS",
        "WHERE userid = #{userid}",
        "  AND workdate BETWEEN TO_DATE(#{start}, 'YYYY-MM-DD') AND TO_DATE(#{end}, 'YYYY-MM-DD')"
    })
    List<AttendStatusDTO> getWeeklyAttendStatus(
        @Param("userid") String userid,
        @Param("start") String start,
        @Param("end")   String end
    );
}

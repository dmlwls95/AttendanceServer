package com.example.Attendance.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.dto.AttendStatusDTO;

@Mapper
public interface WorkStatusMapper {

    /** 주간 근태 요약: DTO(WorkSummaryDTO)와 컬럼명 완전 일치 */
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
        "  AND workdate BETWEEN TO_DATE(#{start}, 'YYYY-MM-DD')",
        "                      AND TO_DATE(#{end},   'YYYY-MM-DD')",
        "ORDER BY workdate"
    })
    List<WorkSummaryDTO> getWeeklyWorkSummary(
        @Param("userid") String userid,
        @Param("start") String start,
        @Param("end")   String end
    );

    /** 주간 출근 상태: DTO(AttendStatusDTO)와 컬럼명 완전 일치 */
    @Select({
        "SELECT",
        "  userid,",
        "  workdate,",
        "  attendstatus,",
        "  arrivaltime",
        "FROM WEEKLY_ATTENDSTATUS",
        "WHERE userid = #{userid}",
        "  AND workdate BETWEEN TO_DATE(#{start}, 'YYYY-MM-DD')",
        "                      AND TO_DATE(#{end},   'YYYY-MM-DD')",
        "ORDER BY workdate"
    })
    List<AttendStatusDTO> getWeeklyAttendStatus(
        @Param("userid") String userid,
        @Param("start") String start,
        @Param("end")   String end
    );
}

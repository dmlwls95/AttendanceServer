package com.example.Attendance.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.Attendance.dto.WorkSummaryDTO;
import com.example.Attendance.dto.AttendStatusDTO;

// 단순 조회 및 뷰 기반 쿼리에 최적화된 MyBatis Mapper 사용.
// 제 수준에선 sql 작성이 용이해 사용했으나, 추후 말씀주시면 면접 종료 후 JPA로 변경 검토 가능합니다.

@Mapper
public interface WorkStatusMapper {

    /** 주간 근태 요약: WorkSummaryDTO와 컬럼명 일치 */
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

    /** 주간 출근 상태: AttendStatusDTO와 컬럼명 완전 일치 */
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

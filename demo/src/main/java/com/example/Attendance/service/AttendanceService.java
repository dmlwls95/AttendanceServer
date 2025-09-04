package com.example.Attendance.service;

import com.example.Attendance.controller.*;
import com.example.Attendance.dto.*;
import com.example.Attendance.dto.DayOfWeekResponse.DayType;
import com.example.Attendance.dto.DayOfWeekResponse.StatusType;
import com.example.Attendance.entity.Attendance;
import com.example.Attendance.entity.AttendanceEvent;
import com.example.Attendance.entity.User;
import com.example.Attendance.repository.AttendanceEventRepository;
import com.example.Attendance.repository.AttendanceRepository;
import com.example.Attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceEventRepository attendanceEventRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messaging;

    // ───────────────────────────────── 출퇴근/외출 기존 로직(변경 최소화)
    public void clockIn(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today).orElse(null);
        if (attendance != null && attendance.getClockIn() != null) {
            throw new IllegalStateException("이미 출근 했습니다");
        }
        if (attendance == null) {
            attendance = Attendance.builder().user(user).date(today).build();
        }
        attendance.setClockIn(LocalDateTime.now());

        if (attendance.getClockIn() != null) {
            LocalDateTime actual = attendance.getClockIn().truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime due = LocalDateTime.of(attendance.getDate(),
                    Optional.ofNullable(user.getWorkStartTime()).orElse(LocalTime.of(9, 0)))
                    .truncatedTo(ChronoUnit.MINUTES);
            attendance.setIsLate(actual.isAfter(due) ? 1 : 0);
        }
        attendanceRepository.save(attendance);
        messaging.convertAndSendToUser(user.getEmail(), "/queue/attendance",
                new AttendanceSignal("CHECKED_IN", LocalDateTime.now()));
    }

    public void clockOut(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다"));
        if (attendance.getClockIn() == null) {
            throw new IllegalStateException("출근 기록이 없어 퇴근을 기록할 수 없습니다.");
        }

        LocalTime startTime = Optional.ofNullable(user.getWorkStartTime()).orElse(LocalTime.of(9, 0));
        LocalTime endTime   = Optional.ofNullable(user.getWorkEndTime()).orElse(LocalTime.of(18, 0));

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        attendance.setClockOut(now);

        LocalDate workDate = attendance.getDate();
        LocalDate endDate  = endTime.isBefore(startTime) ? workDate.plusDays(1) : workDate;
        LocalDateTime shiftStart = LocalDateTime.of(workDate, startTime).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime shiftEnd   = LocalDateTime.of(endDate,   endTime  ).truncatedTo(ChronoUnit.MINUTES);

        LocalDateTime clockIn  = attendance.getClockIn().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime clockOut = now;

        LocalDateTime from = clockIn.isBefore(shiftStart) ? shiftStart : clockIn;
        LocalDateTime to   = clockOut.isAfter(shiftEnd)   ? shiftEnd   : clockOut;

        long overtimeMin = 0;
        if (clockOut.isAfter(shiftEnd)) {
            overtimeMin = Duration.between(shiftEnd, clockOut).toMinutes();
        }
        attendance.setOvertimeMinutes((int) overtimeMin);

        long minutes = 0;
        if (!to.isBefore(from)) minutes = Duration.between(from, to).toMinutes();
        attendance.setTotalHours(minutes / 60.0);

        boolean leftEarly = clockOut.isBefore(shiftEnd);
        attendance.setIsLeftEarly(leftEarly ? 1 : 0);

        attendanceRepository.save(attendance);
    }

    public NormalResponse StartOuting(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다"));
        if (attendance.getClockIn() == null) {
            throw new IllegalStateException("출근 기록이 없어 외출을 기록할 수 없습니다.");
        }
        if (attendance.getOutStart() != null) {
            return NormalResponse.builder().success(false).message("이미 외출한 상태에선 외출할 수 없습니다").build();
        }
        attendance.setOutStart(LocalDateTime.now());
        attendanceRepository.save(attendance);
        messaging.convertAndSendToUser(user.getEmail(), "/queue/attendance",
                new AttendanceSignal("BREAK_OUT", LocalDateTime.now()));
        return NormalResponse.builder().success(true).message("외출 시작").build();
    }

    public NormalResponse EndOuting(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalArgumentException("출근 기록이 없습니다"));
        if (attendance.getClockIn() == null) {
            throw new IllegalStateException("출근 기록이 없어 외출을 기록할 수 없습니다.");
        }
        if (attendance.getOutStart() == null) {
            return NormalResponse.builder().success(false).message("외출하지 않으면 복귀 할 수 없습니다.").build();
        }
        attendance.setOutEnd(LocalDateTime.now());
        attendanceRepository.save(attendance);
        messaging.convertAndSendToUser(user.getEmail(), "/queue/attendance",
                new AttendanceSignal("BREAK_IN", LocalDateTime.now()));
        return NormalResponse.builder().success(true).message("외출 끝").build();
    }

    public AttendanceResponse getTodayAttendance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalStateException("오늘 출근 기록이 없습니다."));
        return AttendanceResponse.builder()
                .date(attendance.getDate())
                .clockIn(attendance.getClockIn())
                .clockOut(attendance.getClockOut())
                .isLate(attendance.getIsLate())
                .isLeftEarly(attendance.getIsLeftEarly())
                .totalHours(attendance.getTotalHours())
                .build();
    }

    // ───────────────────────────────── 요약 API (누적 버그 수정)
    public AttendanceHistoryResponse getSummary(String email, LocalDate from, LocalDate to) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));
        List<Attendance> list = attendanceRepository.findAllByUserAndDateBetween(user, from, to);

        int workdays = 0;
        int worktimes = 0;  // 분
        int overtimes = 0;  // 분
        int absencedays = 0;

        List<UserAttendanceHistory> dtos = list.stream()
                .map(a -> UserAttendanceHistory.builder()
                        .workdate(a.getDate())
                        .clock_in(a.getClockIn())
                        .clock_out(a.getClockOut())
                        .workMinute(a.getTotalHours() == null ? 0 : (int) Math.round(a.getTotalHours() * 60))
                        .overtimeMinute(a.getOvertimeMinutes())
                        .build()
                ).collect(Collectors.toList());

        for (UserAttendanceHistory val : dtos) {
            if (val.getWorkMinute() > 0) workdays++;
            else absencedays++;
            worktimes += val.getWorkMinute();
            overtimes += val.getOvertimeMinute();
        }

        return AttendanceHistoryResponse.builder()
                .workDays(workdays)
                .workTimes(worktimes)
                .overTimes(overtimes)
                .absenceDays(absencedays)
                .historyList(dtos)
                .build();
    }

    public AttendanceMonthlyResponse getMonthlySummary(String email, int year, int month) {
        int nowYear = Year.now().getValue();
        if (year > nowYear) throw new IllegalArgumentException("요청한 연도는 현재 연도보다 클 수 없습니다");
        if (month < 1 || month > 12) throw new IllegalArgumentException("월(month)는 1~12사이의 값이어야 합니다.");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());
        List<Attendance> records = attendanceRepository.findAllByUserAndDateBetween(user, fromDate, toDate);

        int totalDaysWorked = 0;
        double totalHours = 0;
        int totalWorkableDays = getWorkableDays(year, month);

        for (Attendance a : records) {
            if (a.getClockIn() != null) totalDaysWorked++;
            if (a.getClockOut() != null && a.getClockIn() != null && a.getTotalHours() != null) {
                totalHours += a.getTotalHours();
            }
        }
        double averageHours = totalDaysWorked > 0 ? totalHours / totalDaysWorked : 0.0;
        int missedDays = Math.max(0, totalWorkableDays - totalDaysWorked);

        return AttendanceMonthlyResponse.builder()
                .totalDaysWorked(totalDaysWorked)
                .totalHours(totalHours)
                .averageHours(averageHours)
                .missedDays(missedDays)
                .build();
    }

    public static int getWorkableDays(int year, int month) {
        return (int) IntStream.rangeClosed(1, YearMonth.of(year, month).lengthOfMonth())
                .mapToObj(day -> LocalDate.of(year, month, day))
                .filter(d -> !(d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY))
                .count();
    }

    public boolean hasCheckedInToday(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today).orElse(null);
        return attendance != null && attendance.getClockIn() != null;
    }

    public boolean hasBreakOut(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today).orElse(null);
        return attendance != null && attendance.getOutStart() != null;
    }

    public List<AttendanceEventResponse> getRecentAttendance(String email, int howmany) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원 입니다"));
        Pageable pageable = PageRequest.of(0, howmany, Sort.by(Sort.Direction.DESC, "occurredAt"));
        return attendanceEventRepository.findByUser(user, pageable)
                .stream().map(AttendanceEventResponse::from).collect(Collectors.toList());
    }

    public static class AttendanceSignal {
        private final String type;
        private final LocalDateTime at;
        public AttendanceSignal(String type, LocalDateTime at) { this.type = type; this.at = at; }
        public String getType() { return type; }
        public LocalDateTime getAt() { return at; }
    }
    
    
    // ------------------------------
    // 주간 근로 분석 서비스 함수
    //-------------------------------
    public WeeklyDashboardResponse getWeeklyDashboard(String email, LocalDate date) {
        
    	User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        List<Attendance> DB_info = attendanceRepository.findAllByUserAndDateBetweenOrderByDateAsc(user, monday, sunday);
        
        // 날짜별 Map으로 만들기
        Map<LocalDate, Attendance> attendanceMap = DB_info.stream()
            .collect(Collectors.toMap(Attendance::getDate, a -> a));

        // 월~일 루프 돌면서 없는 날짜는 빈 객체로 채우기
        List<Attendance> weekly_info = new ArrayList<>();
        
        for (int i = 0; i < 7; i++) {
        	
            LocalDate dayofweek = monday.plusDays(i);
            
            Attendance attendance = attendanceMap.getOrDefault(dayofweek, 
            		Attendance.builder()
            		.user(user)
                    .date(dayofweek)
                    .clockIn(null)
                    .clockOut(null)
                    .totalHours(0.0)
                    .overtimeMinutes(0)
                    .isLate(0)
                    .isLeftEarly(0)
                    .isAbsence(0)
                    .outStart(null)
                    .outEnd(null)
                    .build());
            
            weekly_info.add(attendance);
        }
        
        
        System.out.println(weekly_info.size());
        
        // DTO에 넣기 위한 Info
        List<DayOfWeekResponse> day_info = new ArrayList<DayOfWeekResponse>();
        long TotalWorktime = 0, TotalOvertime = 0, LeftTime = 0, TotalTime = 0;
        
        WeeklyDashboardResponse dto = new WeeklyDashboardResponse();
        
        for(Attendance weekly_tmp : weekly_info) {
        	DayOfWeekResponse day_tmp = new DayOfWeekResponse();
    		    		
    		// 주말인 상황
        	if(weekly_tmp.getDate().getDayOfWeek().equals(DayOfWeek.SATURDAY) || 
        		weekly_tmp.getDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) 
        	{
        		day_tmp.setDayOfweek(weekly_tmp.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN));
        		day_tmp.setDate(weekly_tmp.getDate());
        		day_tmp.setDayType(DayType.WEEKEND);
        		
        		// 출근 기록이 없다면
				if (weekly_tmp.getClockIn() == null) {
					day_tmp.setClockIn(null);
					day_tmp.setClockOut(null);
					day_tmp.setWorkTime(0);
					day_tmp.setOverTime(0);
					day_tmp.setStatus(StatusType.DEFAULT);
				}
        	}
        	else {
				// 평일 이라면
				day_tmp.setDayOfweek(
						weekly_tmp.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN));
				day_tmp.setDate(weekly_tmp.getDate());
				day_tmp.setWorkTime(calculateWorkTime(weekly_tmp));
				day_tmp.setOverTime(weekly_tmp.getOvertimeMinutes());
				day_tmp.setDayType(DayType.WEEKDAY);
				day_tmp.setStatus(getstatus(weekly_tmp, date));
				day_tmp.setClockIn(weekly_tmp.getClockIn());
				day_tmp.setClockOut(weekly_tmp.getClockOut());
			}
			day_info.add(day_tmp);
			TotalWorktime += day_tmp.getWorkTime();
			TotalOvertime += day_tmp.getOverTime();
			LeftTime = (weekly_info.size() - 2) * 9 * 60 - TotalWorktime;
			TotalTime = TotalWorktime + TotalOvertime;
        }
        
        return WeeklyDashboardResponse.builder()
        		.info(day_info)
        		.totalWorktime(TotalWorktime)
        		.totalOvertime(TotalOvertime)
        		.leftTime(LeftTime)
        		.totalTime(TotalTime)
                .build();
    }

//public  List<Attendance> initList(List<Attendance> info,  User user, LocalDate date){
//	Map<LocalDate, Attendance> attendanceMap = info.stream()
//            .collect(Collectors.toMap(Attendance::getDate, a -> a));
//	
//	List<Attendance> weekly_info = new ArrayList<>();
//	 
//	for (int i = 0; i < 7; i++) {
//    	
//        LocalDate dayofweek = monday.plusDays(i);
//        
//        Attendance attendance = attendanceMap.getOrDefault(dayofweek, 
//        		Attendance.builder()
//        		.user(user)
//                .date(dayofweek)
//                .clockIn(null)
//                .clockOut(null)
//                .totalHours(0.0)
//                .overtimeMinutes(0)
//                .isLate(0)
//                .isLeftEarly(0)
//                .isAbsence(0)
//                .outStart(null)
//                .outEnd(null)
//                .build());
//        
//        weekly_info.add(attendance);
//    }
//	 return weekly_info;
//}

 public long calculateWorkTime(Attendance info) {
		
		long totalWorkTime = 0, totalOutTime = 0;

		// DB에 저장된 실제 출근 시간
		LocalDateTime startTime = LocalDateTime.of(info.getDate(), info.getUser().getWorkStartTime());
		// DB에 저장된 실제 퇴근 시간
		LocalDateTime endTime = LocalDateTime.of(info.getDate(), info.getUser().getWorkEndTime());
		
		
		LocalDateTime clockIn = null, clockOut = null;;
		if (info.getClockIn() != null && info.getClockOut() != null) {
			// 출근 시간이 9시 이전이면 9시로 기록, 이후면 이후 시간 기록
			clockIn = info.getClockIn().isBefore(startTime) == true ? LocalDateTime.of(info.getClockIn().getYear(),
					info.getClockIn().getMonthValue(), info.getClockIn().getDayOfMonth(), 9, 0) : info.getClockIn();

			// 퇴근 시간이 18시 이후면 18시로 기록, 이전이면 이전 시간 기록
			clockOut = info.getClockOut().isAfter(endTime) == true ? LocalDateTime.of(info.getClockOut().getYear(),
					info.getClockOut().getMonthValue(), info.getClockOut().getDayOfMonth(), 18, 0) : info.getClockOut();
		}
		// 외출 시간, 외출 복귀 시간
		LocalDateTime outStart = info.getOutStart();
		LocalDateTime outEnd = info.getOutEnd();
		
		// 출근, 퇴근, 외출 시간,을 모두 고려해서 WorkTime 세팅

		// 출근, 퇴근 둘다 null 값이 아니라면
		if (clockIn != null && clockOut != null) {

			totalWorkTime = Duration.between(clockIn, clockOut).toMinutes();

			if (outStart != null && outEnd != null)
				totalOutTime = Duration.between(outStart, outEnd).toMinutes();
		}
		// 퇴근만 null 값일 때 => 아직 퇴근하지 않았을 경우
		else if (clockIn != null && clockOut == null) {

			totalWorkTime = Duration.between(clockIn, LocalDateTime.now()).toMinutes();

			if (outStart != null && outEnd != null)
				totalOutTime = Duration.between(outStart, outEnd).toMinutes();
		}
		// 둘다 null 값 일 때 -> 결근. 일한 값이 없음.
		else {
			return 0;
		}
		
		return totalWorkTime - totalOutTime;
 }

 public StatusType getstatus(Attendance info, LocalDate date) {
	 
		if (info.getIsLate() == 0 && info.getClockIn() != null &&info.getIsLeftEarly() == 0){return StatusType.NORMAL;} 
		else if (info.getIsLate() == 1) {return StatusType.LATE;} 
		else if (info.getDate().isBefore(date) &&info.getClockIn() == null) {return StatusType.ABSENCE;} 
		else if (info.getIsLeftEarly() == 1) {return StatusType.LEFTEARLY;} 
		else if (info.getIsLate() == 1 && info.getIsLeftEarly() == 1) {return StatusType.LATEANDLEFTEARLY;}
		
		return StatusType.DEFAULT;
 }
 
 // ─────────────────────────────────────────────────────────────
 // 월간 대시보드 계산 (토/일만 휴일로 간주, 공휴일 미포함 요청사항 반영)
 // ─────────────────────────────────────────────────────────────
 public MonthlyDashboardResponse getMonthlyDashboard(String email, int year, int month) {
     User user = userRepository.findByEmail(email)
             .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

     LocalDate from = LocalDate.of(year, month, 1);
     LocalDate to   = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth());

     List<Attendance> records = attendanceRepository.findAllByUserAndDateBetween(user, from, to);

     // 평일(월~금) 수 계산 (Java8 호환: while 루프)
     int workableDays = 0;
     {
         LocalDate t = from;
         while (!t.isAfter(to)) {
             DayOfWeek dw = t.getDayOfWeek();
             if (dw != DayOfWeek.SATURDAY && dw != DayOfWeek.SUNDAY) workableDays++;
             t = t.plusDays(1);
         }
     }

     // 사용자 근무시간 (없으면 09:00~18:00)
     LocalTime startTime = (user.getWorkStartTime() != null) ? user.getWorkStartTime() : LocalTime.of(9, 0);
     LocalTime endTime   = (user.getWorkEndTime()   != null) ? user.getWorkEndTime()   : LocalTime.of(18, 0);

     // 날짜→출결 맵
     Map<LocalDate, Attendance> map = new HashMap<>();
     for (Attendance a : records) map.put(a.getDate(), a);

     int presentDays = 0, lateDays = 0, absentDays = 0, holidayDays = 0;
     int normalMin = 0, overtimeMin = 0, holidayMin = 0, nightMin = 0, lateMin = 0;

     LocalDate d = from;
     while (!d.isAfter(to)) {
         Attendance a = map.get(d);
         boolean weekend = isWeekend(d);

         if (a == null || a.getClockIn() == null) {
             if (weekend) holidayDays++;  // 주말, 출근X → 휴일
             else absentDays++;           // 평일, 출근X → 결근
             d = d.plusDays(1);
             continue;
         }

         // 출근기록이 있는 날
         LocalDateTime ci = a.getClockIn();
         LocalDateTime co = (a.getClockOut() != null) ? a.getClockOut() : a.getClockIn(); // 퇴근 없으면 0분
         if (co.isBefore(ci)) co = ci; // 안전

         // 총 근무분
         long workedMin = Math.max(0, Duration.between(ci, co).toMinutes());

         // 야간(22:00~익일 05:00) 겹친 분
         nightMin += (int) minutesOverlap(
                 ci, co,
                 LocalDateTime.of(d, LocalTime.of(22, 0)),
                 LocalDateTime.of(d.plusDays(1), LocalTime.of(5, 0))
         );

         if (weekend) {
             // 주말 근무 → 휴일근무로 전부 집계
             holidayMin += (int) workedMin;
             presentDays++; // 주말 출근도 "출근일"에 포함
         } else {
             // 평일
             presentDays++;

             // 지각일/지각분
             LocalDateTime shiftStart = LocalDateTime.of(d, startTime);
             if (ci.isAfter(shiftStart)) {
                 lateDays++;
                 lateMin += (int) Duration.between(shiftStart, ci).toMinutes();
             }

             // 평일 소정근로 분 = [실근무] ∩ [start~end]
             LocalDate endDate = endTime.isBefore(startTime) ? d.plusDays(1) : d;
             LocalDateTime shiftEnd = LocalDateTime.of(endDate, endTime);

             long inside = minutesOverlap(ci, co, shiftStart, shiftEnd);
             normalMin += (int) inside;

             // 평일 잔업(범위 외) = total - inside
             overtimeMin += (int) Math.max(0, workedMin - inside);
         }

         d = d.plusDays(1);
     }

     int totalMinutes = normalMin + overtimeMin + holidayMin; // (night은 겹침 구간)

     return MonthlyDashboardResponse.builder()
             .presentDays(presentDays)
             .lateDays(lateDays)
             .absentDays(absentDays)
             .holidayDays(holidayDays)
             .normalMinutes(normalMin)
             .overtimeMinutes(overtimeMin)
             .holidayMinutes(holidayMin)
             .nightMinutes(nightMin)
             .workableDays(workableDays)
             .lateMinutes(lateMin)
             .totalMinutes(totalMinutes)
             .build();
 }

 // ─────────────────────────────────────────────────────────────
 // 주간 KPI (월요일 ~ to(접속일)까지) : late/over/total 분
 // ─────────────────────────────────────────────────────────────
 public WeeklyKpiResponse getWeeklyKpi(String email, LocalDate from, LocalDate to) {
     User user = userRepository.findByEmail(email)
             .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

     List<Attendance> list = attendanceRepository.findAllByUserAndDateBetween(user, from, to);

     LocalTime startTime = (user.getWorkStartTime() != null) ? user.getWorkStartTime() : LocalTime.of(9, 0);
     LocalTime endTime   = (user.getWorkEndTime()   != null) ? user.getWorkEndTime()   : LocalTime.of(18, 0);

     int lateMin = 0;
     int overMin = 0;
     int totalMin = 0;

     for (Attendance a : list) {
         if (a.getClockIn() == null || a.getClockOut() == null) continue;

         LocalDate d = a.getDate();
         LocalDateTime ci = a.getClockIn();
         LocalDateTime co = a.getClockOut();
         if (co.isBefore(ci)) co = ci;

         // 총 근무
         int worked = (int) Math.max(0, Duration.between(ci, co).toMinutes());
         totalMin += worked;

         // 지각
         LocalDateTime shiftStart = LocalDateTime.of(d, startTime);
         if (ci.isAfter(shiftStart)) {
             lateMin += (int) Duration.between(shiftStart, ci).toMinutes();
         }

         // 소정/잔업
         LocalDate endDate = endTime.isBefore(startTime) ? d.plusDays(1) : d;
         LocalDateTime shiftEnd = LocalDateTime.of(endDate, endTime);

         int inside = (int) minutesOverlap(ci, co, shiftStart, shiftEnd);
         overMin += Math.max(0, worked - inside); // 범위 외
     }

     return WeeklyKpiResponse.builder()
             .lateMinutes(lateMin)
             .overMinutes(overMin)
             .totalMinutes(totalMin)
             .build();
 }

 // ─ helpers (기존 그대로 사용)
 private boolean isWeekend(LocalDate date) {
     DayOfWeek dw = date.getDayOfWeek();
     return (dw == DayOfWeek.SATURDAY || dw == DayOfWeek.SUNDAY);
 }
 
 private long minutesOverlap(LocalDateTime a1, LocalDateTime a2,
                             LocalDateTime b1, LocalDateTime b2) {
     LocalDateTime start = a1.isAfter(b1) ? a1 : b1; // max
     LocalDateTime end   = a2.isBefore(b2) ? a2 : b2; // min
     if (end.isAfter(start)) {
         return Duration.between(start, end).toMinutes();
     }
     return 0;
 }
 
 //csv
 public byte[] exportCsv(User user, LocalDate start, LocalDate end) {
	    StringBuilder sb = new StringBuilder();
	    // CSV 헤더
	    sb.append("근무일,출근시간,퇴근시간,근무시간,잔업시간\r\n");

	    List<Attendance> records = attendanceRepository.findByUserAndDateBetween(user, start, end);

	    for (Attendance r : records) {
	        String workDate  = r.getDate() != null ? r.getDate().toString() : "";
	        String clockIn   = r.getClockIn() != null ? r.getClockIn().toString() : "";
	        String clockOut  = r.getClockOut() != null ? r.getClockOut().toString() : "";

	        // 근무시간(분 단위 계산 → "시:분" 변환)
	        int workMin = calcWorkMinutes(r.getClockIn(), r.getClockOut());
	        String workTime = toHhMm(workMin);

	        // 잔업시간 (DB 필드 overtimeMinutes → "시:분" 변환)
	        String overtimeTime = toHhMm(r.getOvertimeMinutes());

	        sb.append(workDate).append(',')
	          .append(clockIn).append(',')
	          .append(clockOut).append(',')
	          .append(workTime).append(',')
	          .append(overtimeTime)
	          .append("\r\n");
	    }

	    return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

	private String toHhMm(int min) {
	    int h = min / 60;
	    int m = min % 60;
	    return String.format("%d 시간 %02d 분", h, m);
	}

	private int calcWorkMinutes(LocalDateTime in, LocalDateTime out) {
	    if (in == null || out == null) return 0;
	    long mins = ChronoUnit.MINUTES.between(in, out);
	    return (int) Math.max(0, mins);
	}
}

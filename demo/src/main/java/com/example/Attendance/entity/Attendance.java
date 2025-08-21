package com.example.Attendance.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_seq")
    @SequenceGenerator(name = "attendance_seq", sequenceName = "ATTENDANCE_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id") // 명시적으로 지정
    private User user;

    @Column(name = "work_date")  // 예약어 회피 + 소문자 권장
    private LocalDate date;

    @Column(name = "clock_in")
    private LocalDateTime clockIn;

    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    @Column(name = "total_hours")
    private Double totalHours;
    
    @Column(name = "overtime_minutes")
    private int overtimeMinutes;

    @Column(name = "is_late")
    @Builder.Default
    private Integer isLate = 0;

    @Column(name = "is_left_early")
    @Builder.Default
    private Integer isLeftEarly = 0;
    
    @Column(name = "absence")
    @Builder.Default
    private Integer isAbsence = 0;
    
    @Column(name = "out_start")
    private LocalDateTime outStart;
    
    @Column(name = "out_end")
    private LocalDateTime outEnd;
}


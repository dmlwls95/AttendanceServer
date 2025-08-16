package com.example.Attendance.entity;


import lombok.*;
import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "leave")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_seq")
    @SequenceGenerator(name = "leave_seq", sequenceName = "LEAVE_SEQ", allocationSize = 1)
    private Long leaveId;

    // 연차, 병가, 휴직 등 구분
    @Column(nullable = false, length = 50)
    private String leaveType;

    // 휴가 시작일
    @Column(nullable = false)
    private LocalDate startDate;

    // 휴가 종료일
    @Column(nullable = false)
    private LocalDate endDate;

    // 사유
    @Column(length = 500)
    private String reason;

    // 신청 상태 (예: PENDING, APPROVED, REJECTED)
    @Column(nullable = false, length = 20)
    private String status;

    // 신청자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

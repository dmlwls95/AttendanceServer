package com.example.Attendance.entity;


import lombok.*;
import java.time.LocalDate;
import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // 명시적으로 지정
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}

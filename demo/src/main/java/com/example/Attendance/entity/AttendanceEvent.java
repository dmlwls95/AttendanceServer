package com.example.Attendance.entity;

import lombok.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "attendance_events"
)
@SequenceGenerator(
    name = "attendance_events_seq",
    sequenceName = "ATTENDANCE_EVENTS_SEQ",
    allocationSize = 1
)
public class AttendanceEvent {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_events_seq")
    @SequenceGenerator(name = "attendance_events_seq", sequenceName = "ATTENDANCE_EVENTS_SEQ", allocationSize = 1)
    private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // 명시적으로 지정
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    /**
     * 발생 시각(사용자 액션이 일어난 실제 시각)
     * Oracle: TIMESTAMP WITH TIME ZONE에 매핑됨
     */
    @NotNull
    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private SourceType source;

    /** 클라이언트 IP 또는 디바이스 식별자 */
    @Column(name = "ip_or_device", length = 64)
    private String ipOrDevice;

    /** 선택: 사용자 에이전트 문자열(웹일 경우) */
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    /** 비고(관리자 보정 사유 등) */
    @Column(name = "note", length = 4000)
    private String note;

    /**
     * 동일 트랜잭션/요청 상관관계 식별을 위한 키(옵션)
     * 예: 한 번의 요청에서 여러 이벤트가 쌓일 때 묶어서 추적
     */
    @Column(name = "correlation_id")
    private String correlationId;

    /** 레코드 생성 시각(서버 기준) */
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public enum EventType {
        CLOCK_IN,      // 출근
        BREAK_OUT,     // 외출 시작
        BREAK_IN,      // 외출 복귀
        CLOCK_OUT,     // 퇴근
        CORRECTION     // 보정(관리자 수정용 이벤트)
    }

    public enum SourceType {
        WEB,
        MOBILE,
        ADMIN,
        BATCH
    }
}

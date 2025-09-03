package com.example.Attendance.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOARD_COMMENT") // Oracle 예약어 회피 , SQL에서 조회시 대문자로 조회되어 변경해둠.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_comment_seq")
    @SequenceGenerator(name = "board_comment_seq", sequenceName = "board_comment_seq", allocationSize = 1)
    private Long id;

    private String writer;

    @Column(columnDefinition = "CLOB")
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ✅ 댓글이 속한 게시글 (Board)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}
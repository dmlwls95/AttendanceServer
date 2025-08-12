package com.example.Attendance.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_seq")
    @SequenceGenerator(name = "board_seq", sequenceName = "board_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "recommend_count", nullable = false)
    private int recommendCount;
    
    private String title;
    private String content;
    private String writer;
    private LocalDateTime writeDate;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    /* ───────── 댓글 컬렉션 ───────── */
    @OneToMany(mappedBy = "board",
               cascade = CascadeType.ALL,   // Board 삭제 → 댓글 자동 삭제
               orphanRemoval = true)        // 컬렉션에서 빠진 댓글도 삭제
    private List<Comment> comments = new ArrayList<>();
    
    /*------------추천 컬렉션-----------*/
    public int getRecommendCount() { return recommendCount; }
    public void setRecommendCount(int recommendCount) { this.recommendCount = recommendCount; }
}
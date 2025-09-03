package com.example.Attendance.dto;

import lombok.*;
import java.time.LocalDateTime;

import com.example.Attendance.entity.BoardType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime writeDate = LocalDateTime.now();
    private BoardType boardType; // "FREE", "NOTICE", "SUGGEST"
    private int recommendCount;
    private int commentCount;    // 댓글수  

}
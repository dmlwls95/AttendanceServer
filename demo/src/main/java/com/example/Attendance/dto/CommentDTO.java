package com.example.Attendance.dto;

import lombok.*;
import java.time.LocalDateTime;
import com.example.Attendance.entity.BoardType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private BoardType boardType;
    private String writer;
    private String content;
    private Long boardId;
    private LocalDateTime createdAt;
}
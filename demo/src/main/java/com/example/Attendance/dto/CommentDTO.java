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
    private String boardType;
    private String writer; // 닉네임 (댓글 수정/삭제 버튼 수정 위해 -> 프론트에 노출되는 값)
    private String content;
    private Long boardId;
    private LocalDateTime createdAt;

    private boolean isOwner; // ← 로그인한 사용자인지 여부 (댓글 수정/삭제 버튼 조건)
}
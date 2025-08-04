package com.example.Attendance.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_seq")
	@SequenceGenerator(name = "board_seq", sequenceName = "board_seq", allocationSize = 1)
	private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime writeDate;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;
}
package com.example.Attendance.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BOARD_CATEGORY")
public class BoardCategory {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "BOARD_TYPE", length = 20)
    private BoardType boardType;

    @Column(name = "NAME")
    private String name;

    @Column(name = "WRITE_PERMISSION")
    private boolean writePermissionAdminOnly;
}

package com.example.Attendance.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
    private int id;
    private String title;
    private String content;
    private String writer;
    private Date writeDate;

    // 기본 생성자
    public Board() {
    	
    }

}
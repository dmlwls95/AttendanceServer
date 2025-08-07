package com.example.Attendance.dto;


import java.util.Date;
import lombok.Data;

@lombok.Data
public class AttendStatusDTO {

	private String userid;
	private Date workdate;
	private String attendstatus;
	private Date arrivaltime;
}

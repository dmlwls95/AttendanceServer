package com.example.Attendance.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class WorkSummaryDTO {

	private String userid;
	private Date workdate;
	private String dayofweek;
	private BigDecimal workinghours;
	private BigDecimal extendhours;
	private BigDecimal totalhours;
	private String workstatus;
	private Date arrivaltime;
	
}

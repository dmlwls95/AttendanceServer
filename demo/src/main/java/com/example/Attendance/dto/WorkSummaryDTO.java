package com.example.Attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class WorkSummaryDTO {

    private String userid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date workdate;

    private String dayofweek;
    private BigDecimal workinghours;
    private BigDecimal extendhours;
    private BigDecimal totalhours;
    private String workstatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private Date arrivaltime;
}
package com.fms.demoFMS.DTOS.AddEntity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResponseStudentDto {

    private Integer studentid;
    private String studentname;
    private String fathername;
    private String email;
    private double feebalance;
    private LocalDate birthdate;

    // Assigned Teacher Details
    private Integer teacherId;
    private String teacherName;
    private String teacherEmail;
    private Integer teacherPhone;
}

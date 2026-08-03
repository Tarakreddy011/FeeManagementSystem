package com.fms.demoFMS.DTOS.AddEntity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseStudentDto {

    private Integer studentid;
    private String studentname;
    private String fathername;
    private String email;
    private double feebalance;
}

package com.fms.demoFMS.DTOS;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseStudentDto {

    private Integer id;
    private String studentname;
    private String fatherName;
    private String email;
    private int feebalance;
}

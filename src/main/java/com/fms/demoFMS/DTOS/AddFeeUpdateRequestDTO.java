package com.fms.demoFMS.DTOS;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFeeUpdateRequestDTO {
    private Integer studentid;
    private int feebalance;
    private String studentname;
   private Integer teacherid;
    private double newfee;
    private String fathername;
    private String teachername;
}


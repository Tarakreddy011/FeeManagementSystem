package com.fms.demoFMS.DTOS.AddFee;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFeeUpdateRequestDTO {
    private Integer studentid;
    private int feebalance;
    private String studentname;
   private Integer teacherid;
   // private double newfee;
    private String email;
//    private String fathername;
//    private String teachername;
}


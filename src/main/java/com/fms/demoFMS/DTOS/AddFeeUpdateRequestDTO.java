package com.fms.demoFMS.DTOS;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFeeUpdateRequestDTO {
    private Integer studentid;
    private int feebalance;
    private String studentname;
   // private String teachername;
    private double newfee;

}

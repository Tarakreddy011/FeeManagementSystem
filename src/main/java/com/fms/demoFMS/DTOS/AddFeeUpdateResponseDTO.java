package com.fms.demoFMS.DTOS;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFeeUpdateResponseDTO {

    private Integer studentid;
    private Integer teaherid;
    private String studentname;
    private String fatherName;
    private String email;
    private int feebalance;
    private String teachername;
    private double newfee;


}

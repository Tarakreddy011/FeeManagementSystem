package com.fms.demoFMS.DTOS.AddFee;


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
    private String TeacherName;
    private double newfee;


}

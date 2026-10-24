package com.fms.demoFMS.DTOS.AddFee;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddFeeUpdateRequestDTO {

    @JsonAlias({"studentid", "studentId", "student_id"})
    private Integer studentid;

    @JsonAlias({"feebalance", "feeBalance", "amount", "newfee", "newFee"})
    private double feebalance;

    @JsonAlias({"studentname", "studentName", "name"})
    private String studentname;

    @JsonAlias({"teacherid", "teacherId", "teacher_id"})
    private Integer teacherid;

    private String email;
}

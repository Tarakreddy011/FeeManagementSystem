package com.fms.demoFMS.DTOS.FeeRecpitDTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFeePaymentDTO {

    private double amount;

    @JsonAlias({"studentId", "studentid", "student_id"})
    private Integer studentId;

    @JsonAlias({"teacherId", "teacherid", "teacher_id"})
    private Integer teacherId;

}

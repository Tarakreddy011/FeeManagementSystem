package com.fms.demoFMS.DTOS.FeeRecpitDTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ResponseFeePaymentDTO {

    private Integer feepaymentId;
    LocalDate date;
    LocalTime time;
    private double amount;
    private Integer studentId;
    private Integer teacherId;
    //private Double feebalance;
    private Double newfeebalanceafterpayment;

}

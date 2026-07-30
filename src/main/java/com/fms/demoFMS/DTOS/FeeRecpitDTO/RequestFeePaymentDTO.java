package com.fms.demoFMS.DTOS.FeeRecpitDTO;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFeePaymentDTO {

    private double amount;
    private Integer studentId;
    private Integer teacherId;


}
/**
 *
 //private Integer feepaymentId;
 //LocalDate date;
 //LocalTime time;
 */

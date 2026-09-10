package com.fms.demoFMS.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "feehistory")
@Getter
@Setter
public class FeeHistory {

    LocalDate date;
    LocalTime time;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feepaymentId;
    private double amount;


    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private PrincipalEntity teacher;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private StudentEntity student;
}

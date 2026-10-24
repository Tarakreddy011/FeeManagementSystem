package com.fms.demoFMS.DTOS.AddEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AddStudentdto {

    private Integer id;
    private String studentname;
    private String fathername;
    private String email;
    private double feebalance;
    private LocalDate birthdate;

    @JsonAlias({"teacherId", "teacherid", "teacher_id"})
    private Integer teacherId;
}

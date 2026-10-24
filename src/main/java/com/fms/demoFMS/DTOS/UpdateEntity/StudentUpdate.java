package com.fms.demoFMS.DTOS.UpdateEntity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentUpdate {

    @JsonAlias({"studentname", "student_name", "name"})
    private String studentname;

    @JsonAlias({"fathername", "fatherName", "father_name"})
    private String fatherName;

    @JsonAlias({"email", "student_email"})
    private String email;

    @JsonAlias({"feebalance", "fee_balance", "feeBalance"})
    private Double feebalance;

    @JsonAlias({"teacherId", "teacher_id", "teacherid"})
    private Integer teacherId;

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFathername() {
        return fatherName;
    }

    public void setFathername(String fathername) {
        this.fatherName = fathername;
    }
}


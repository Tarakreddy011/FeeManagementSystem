package com.fms.demoFMS.DTOS.AddFee;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("teacherid")
    public Integer getTeacherid() {
        return teaherid;
    }

    public void setTeacherid(Integer teacherid) {
        this.teaherid = teacherid;
    }

    @JsonProperty("teacherName")
    public String getTeachername() {
        return TeacherName;
    }

    public void setTeachername(String teachername) {
        this.TeacherName = teachername;
    }

    public void setTeacherName(String teacherName) {
        this.TeacherName = teacherName;
    }

    public String getTeacherName() {
        return TeacherName;
    }
}

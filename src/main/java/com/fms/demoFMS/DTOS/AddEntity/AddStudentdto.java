package com.fms.demoFMS.DTOS.AddEntity;


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
    private int feebalance;
    private LocalDate birthdate;

}

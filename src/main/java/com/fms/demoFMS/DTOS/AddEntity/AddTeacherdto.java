package com.fms.demoFMS.DTOS.AddEntity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class AddTeacherdto {


   // private int id;
    private Integer id;
    private String teachername;
    private String email;
    private int phone;
    private boolean isprincipal;
 private LocalDate birthdate;

}

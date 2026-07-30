package com.fms.demoFMS.DTOS.UpdateEntity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherUpdate {

   // private int id;
    private String teachername;
    //private String email;     <-- this feild should not be updated -->
    private int phone;
    private boolean isprincipal;

}

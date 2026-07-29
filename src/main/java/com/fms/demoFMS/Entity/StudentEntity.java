package com.fms.demoFMS.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="student")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentid;
    private String studentname;
    private String fatherName;
    @Email
    private String email;
    @NotNull
    private int feebalance;


    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private PrincipalEntity teacher;

}

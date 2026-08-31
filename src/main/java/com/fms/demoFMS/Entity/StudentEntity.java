package com.fms.demoFMS.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@Table(name="student")
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentid;
    private String studentname;
    private String password;

    private LocalDate birthdate;
    private String fathername;
    @Email
    private String email;
    private double feebalance;


    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private PrincipalEntity teacher;

}

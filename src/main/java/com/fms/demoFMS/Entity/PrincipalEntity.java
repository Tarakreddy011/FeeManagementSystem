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
@Table(name="teacher")
public class PrincipalEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teacherid;

    private String teachername;

    @Email
    private String email;
    private int phone;

    private LocalDate birthdate;
    private String password;

    private String isprincipal;

}

package com.fms.demoFMS.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="teacher")
public class PrincipalEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String teachername;

    @Email
    private String email;
    private int phone;

    private boolean isprincipal;
}

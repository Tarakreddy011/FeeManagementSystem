package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.AddStudentdto;
import com.fms.demoFMS.DTOS.ResponseStudentDto;
import com.fms.demoFMS.DTOS.ResponseTeacherdto;
import com.fms.demoFMS.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StudentController {


    StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/addstudent")
    public ResponseEntity<?> addStudent(@RequestBody AddStudentdto addStudentdto) {
        ResponseStudentDto result = studentService.addstudent(addStudentdto);
        return ResponseEntity.status(201).body(result);
    }
}

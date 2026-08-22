package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.AddEntity.AddStudentdto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseStudentDto;
import com.fms.demoFMS.DTOS.UpdateEntity.StudentUpdate;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/student")
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

    @GetMapping("/getstudent/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Integer id) {
        ResponseStudentDto responseStudentDto = studentService.getstudentById(id);
        return ResponseEntity.status(200).body(responseStudentDto);
    }

    /*public ResponseEntity<?> getstudentbyname(@RequestParam String name) {
        try{
            StudentEntity studentEntity = studentService.getstudentByname(name);
            return ResponseEntity.status(200).body(studentEntity);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }*/
    @GetMapping("/getstudent/byname")
    public ResponseEntity<?> getstudentbyname(@RequestParam String name) {
        try {
            StudentEntity studentEntity = studentService.getstudentByname(name);
            return ResponseEntity.status(200).body(studentEntity);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Student not found");
        }
    }


    @PutMapping("/updatestudent/{id}")
    public ResponseEntity<?> updateStudentByID(@PathVariable Integer id, StudentUpdate studentUpdate) {
        ResponseStudentDto responseStudentDto = studentService.updateStudentByID(id, studentUpdate);
        return ResponseEntity.status(201).body(responseStudentDto);
    }

    @DeleteMapping("/deletestudent/{id}")
    public ResponseEntity<?> deleteStudentByID(@PathVariable Integer id) {
        StudentEntity result = studentService.deleteStudentByID(id);

        return ResponseEntity.status(200).body(result);
    }
}

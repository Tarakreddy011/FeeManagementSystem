package com.fms.demoFMS.Controller;


import com.fms.demoFMS.DTOS.AddTeacherdto;
import com.fms.demoFMS.DTOS.ResponseTeacherdto;
import com.fms.demoFMS.DTOS.TeacherUpdate;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class TeacherController {

    TeacherService teacherService;
    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/addteacher")
    public ResponseEntity<?> addTeacher(@RequestBody AddTeacherdto addTeacherdto) {
        ResponseTeacherdto result = teacherService.addTeacher(addTeacherdto);
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/getteacher/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable Integer id) {
        ResponseTeacherdto principal = teacherService.getTeacherById(id);
        return ResponseEntity.status(200).body(principal);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTeacherById(@PathVariable Integer id, @RequestBody TeacherUpdate teacherUpdate) {
        ResponseTeacherdto result = teacherService.updateTeacherinfo(id, teacherUpdate);
        return ResponseEntity.status(201).body(result);
    }

    @DeleteMapping("/deleteteacher/{id}")
    public ResponseEntity<?> deleteTeacherById(@PathVariable Integer id) {
        PrincipalEntity principalEntity = teacherService.deleteTeacher(id);
        if (principalEntity == null) {
            return ResponseEntity.status(400).body("Invalid input");
        }
        return ResponseEntity.status(200).body("Teacher deleted");
    }

}

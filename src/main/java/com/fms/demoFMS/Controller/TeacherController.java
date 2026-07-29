package com.fms.demoFMS.Controller;


import com.fms.demoFMS.DTOS.AddTeacherdto;
import com.fms.demoFMS.DTOS.ResponseTeacherdto;
import com.fms.demoFMS.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeacherController {

    TeacherService teacherService;
    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/addTeacher")
    public ResponseEntity<?> addTeacher(@RequestBody AddTeacherdto addTeacherdto) {
        ResponseTeacherdto result = teacherService.addTeacher(addTeacherdto);
        return ResponseEntity.status(201).body(result);
    }
}

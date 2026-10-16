package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.AddEntity.AddTeacherdto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseTeacherdto;
import com.fms.demoFMS.DTOS.UpdateEntity.TeacherUpdate;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

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

    @GetMapping("/all")
    public ResponseEntity<?> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/getteacher/name")
    public ResponseEntity<?> getTeacherbyname(@RequestParam String name) {
        try {
            PrincipalEntity teacher = teacherService.getTeacherByName(name);
            return ResponseEntity.status(200).body(teacher);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Teacher not found with name: " + name);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTeacherById(@PathVariable Integer id, @RequestBody TeacherUpdate teacherUpdate) {
        ResponseTeacherdto result = teacherService.updateTeacherinfo(id, teacherUpdate);
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("/deleteteacher/{id}")
    public ResponseEntity<?> deleteTeacherById(@PathVariable Integer id) {
        PrincipalEntity principalEntity = teacherService.deleteTeacher(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Teacher deleted successfully");
        response.put("deletedTeacherId", principalEntity.getTeacherid());
        return ResponseEntity.status(200).body(response);
    }
}

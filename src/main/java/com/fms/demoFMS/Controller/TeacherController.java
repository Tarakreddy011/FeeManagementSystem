package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.AddEntity.AddTeacherdto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseStudentDto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseTeacherdto;
import com.fms.demoFMS.DTOS.UpdateEntity.TeacherUpdate;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import com.fms.demoFMS.Service.StudentService;
import com.fms.demoFMS.Service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/teachers", "/api/teacher"})
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherRepo teacherRepo;
    private final StudentRepo studentRepo;
    private final StudentService studentService;

    @Autowired
    public TeacherController(TeacherService teacherService, TeacherRepo teacherRepo, 
                             StudentRepo studentRepo, StudentService studentService) {
        this.teacherService = teacherService;
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
        this.studentService = studentService;
    }

    // Teacher Self-Service: Current Teacher Profile
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentTeacher(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        String username = authentication.getName();
        PrincipalEntity teacher = teacherRepo.findByEmailIgnoreCase(username)
                .or(() -> teacherRepo.findByTeachernameIgnoreCase(username))
                .or(() -> teacherRepo.findByEmail(username))
                .or(() -> teacherRepo.findByTeachername(username))
                .orElse(null);

        if (teacher == null) {
            return ResponseEntity.status(404).body("Teacher profile not found for user: " + username);
        }
        return ResponseEntity.ok(teacherService.maptoResponseTeacherdto(teacher));
    }

    // Teacher Self-Service: Students assigned to this teacher
    @GetMapping("/my-students")
    public ResponseEntity<?> getMyAssignedStudents(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        String username = authentication.getName();
        PrincipalEntity teacher = teacherRepo.findByEmailIgnoreCase(username)
                .or(() -> teacherRepo.findByTeachernameIgnoreCase(username))
                .or(() -> teacherRepo.findByEmail(username))
                .or(() -> teacherRepo.findByTeachername(username))
                .orElse(null);

        if (teacher == null) {
            return ResponseEntity.status(404).body("Teacher profile not found for user: " + username);
        }

        List<StudentEntity> assigned = studentRepo.findByTeacher_Teacherid(teacher.getTeacherid());
        List<ResponseStudentDto> dtoList = assigned.stream()
                .map(studentService::mapToResponseStudent)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
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
        if (studentRepo.existsByTeacher_Teacherid(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "Reassign or unassign this teacher's students before deleting the faculty record."
            ));
        }

        try {
            PrincipalEntity principalEntity = teacherService.deleteTeacher(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Teacher deleted successfully");
            response.put("deletedTeacherId", principalEntity.getTeacherid());
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "This faculty record is linked to other records and cannot be deleted yet."
            ));
        }
    }
}


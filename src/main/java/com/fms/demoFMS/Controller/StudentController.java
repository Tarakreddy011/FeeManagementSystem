package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.AddEntity.AddStudentdto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseStudentDto;
import com.fms.demoFMS.DTOS.FeeRecpitDTO.RequestFeePaymentDTO;
import com.fms.demoFMS.DTOS.FeeRecpitDTO.ResponseFeePaymentDTO;
import com.fms.demoFMS.DTOS.UpdateEntity.StudentUpdate;
import com.fms.demoFMS.Entity.FeeHistory;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.FeeService.FeeRecipt;
import com.fms.demoFMS.Repos.FeeHistoryRepo;
import com.fms.demoFMS.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/student", "/api/student"})
public class StudentController {

    private final StudentService studentService;
    private final FeeHistoryRepo feeHistoryRepo;
    private final FeeRecipt feeRecipt;

    @Autowired
    public StudentController(StudentService studentService, FeeHistoryRepo feeHistoryRepo, FeeRecipt feeRecipt) {
        this.studentService = studentService;
        this.feeHistoryRepo = feeHistoryRepo;
        this.feeRecipt = feeRecipt;
    }

    // Student Self-Service: Current Student Profile
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentStudent(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        try {
            StudentEntity student = studentService.getStudentByEmailOrUsername(authentication.getName());
            return ResponseEntity.ok(studentService.mapToResponseStudent(student));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Student profile not found for user: " + authentication.getName());
        }
    }

    // Student Self-Service: Current Student Payment History
    @GetMapping("/me/payments")
    public ResponseEntity<?> getCurrentStudentPayments(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        try {
            StudentEntity student = studentService.getStudentByEmailOrUsername(authentication.getName());
            List<FeeHistory> history = feeHistoryRepo.findByStudent_StudentidOrderByFeepaymentIdDesc(student.getStudentid());
            List<Map<String, Object>> records = history.stream().map(h -> {
                Map<String, Object> map = new HashMap<>();
                map.put("paymentId", h.getFeepaymentId());
                map.put("amount", h.getAmount());
                map.put("date", h.getDate() != null ? h.getDate().toString() : "");
                map.put("time", h.getTime() != null ? h.getTime().toString() : "");
                map.put("teacherName", h.getTeacher() != null ? h.getTeacher().getTeachername() : "N/A");
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Could not load payment records: " + e.getMessage());
        }
    }

    // Student Self-Service: Pay Own Fee
    @PostMapping("/me/pay")
    public ResponseEntity<?> payOwnFee(@RequestBody RequestFeePaymentDTO request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        try {
            StudentEntity student = studentService.getStudentByEmailOrUsername(authentication.getName());
            request.setStudentId(student.getStudentid());
            ResponseFeePaymentDTO response = feeRecipt.feereciptgeneration(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/getstudent/byname")
    public ResponseEntity<?> getstudentbyname(@RequestParam String name) {
        try {
            StudentEntity studentEntity = studentService.getstudentByname(name);
            return ResponseEntity.status(200).body(studentEntity);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Student not found with name: " + name);
        }
    }

    @PutMapping("/updatestudent/{id}")
    public ResponseEntity<?> updateStudentByID(@PathVariable Integer id, @RequestBody StudentUpdate studentUpdate) {
        ResponseStudentDto responseStudentDto = studentService.updateStudentByID(id, studentUpdate);
        return ResponseEntity.status(200).body(responseStudentDto);
    }

    @DeleteMapping("/deletestudent/{id}")
    public ResponseEntity<?> deleteStudentByID(@PathVariable Integer id) {
        StudentEntity result = studentService.deleteStudentByID(id);
        return ResponseEntity.status(200).body(result);
    }
}


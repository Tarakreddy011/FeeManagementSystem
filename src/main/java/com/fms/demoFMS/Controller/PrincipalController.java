package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.AddFee.AddFeeUpdateRequestDTO;
import com.fms.demoFMS.DTOS.AddFee.AddFeeUpdateResponseDTO;
import com.fms.demoFMS.Entity.FeeHistory;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.FeeHistoryRepo;
import com.fms.demoFMS.Repos.PrincipalRepo;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import com.fms.demoFMS.Service.FeeUpdateService;
import com.fms.demoFMS.Service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/principal")
public class PrincipalController {

    private final StudentRepo studentRepo;
    private final TeacherRepo teacherRepo;
    private final FeeHistoryRepo feeHistoryRepo;
    private final PrincipalRepo principalRepo;
    private final FeeUpdateService feeUpdateService;
    private final StudentService studentService;

    public PrincipalController(StudentRepo studentRepo, TeacherRepo teacherRepo,
                               FeeHistoryRepo feeHistoryRepo, PrincipalRepo principalRepo,
                               FeeUpdateService feeUpdateService, StudentService studentService) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
        this.feeHistoryRepo = feeHistoryRepo;
        this.principalRepo = principalRepo;
        this.feeUpdateService = feeUpdateService;
        this.studentService = studentService;
    }

    // 1. Executive Analytics & Health Metrics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPrincipalStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalStudents = studentRepo.count();
        long totalTeachers = teacherRepo.count();
        Double totalFeesCollected = feeHistoryRepo.getTotalFeesCollected();
        if (totalFeesCollected == null) totalFeesCollected = 0.0;

        List<StudentEntity> allStudents = studentRepo.findAll();
        double totalOutstanding = allStudents.stream()
                .mapToDouble(StudentEntity::getFeebalance)
                .sum();
        totalOutstanding = Math.round(totalOutstanding * 100.0) / 100.0;

        long unassignedCount = allStudents.stream()
                .filter(s -> s.getTeacher() == null)
                .count();

        double totalBilled = totalFeesCollected + totalOutstanding;
        double recoveryRate = totalBilled > 0 ? (totalFeesCollected / totalBilled) * 100.0 : 0.0;
        recoveryRate = Math.round(recoveryRate * 10.0) / 10.0;

        stats.put("totalStudents", totalStudents);
        stats.put("totalTeachers", totalTeachers);
        stats.put("totalFeesCollected", totalFeesCollected);
        stats.put("totalOutstandingFees", totalOutstanding);
        stats.put("totalBilled", Math.round(totalBilled * 100.0) / 100.0);
        stats.put("recoveryRate", recoveryRate);
        stats.put("unassignedStudentsCount", unassignedCount);
        stats.put("assignedStudentsCount", totalStudents - unassignedCount);
        stats.put("totalTransactions", feeHistoryRepo.count());
        stats.put("systemStatus", "Active & Operational");

        return ResponseEntity.ok(stats);
    }

    // 2. Student-Teacher Relationship Roster
    @GetMapping("/relationships")
    public ResponseEntity<List<Map<String, Object>>> getStudentTeacherRelationships() {
        List<StudentEntity> students = studentRepo.findAll();
        List<Map<String, Object>> list = students.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", s.getStudentid());
            map.put("studentName", s.getStudentname());
            map.put("studentEmail", s.getEmail());
            map.put("fatherName", s.getFathername());
            map.put("feeBalance", s.getFeebalance());

            if (s.getTeacher() != null) {
                map.put("teacherId", s.getTeacher().getTeacherid());
                map.put("teacherName", s.getTeacher().getTeachername());
                map.put("teacherEmail", s.getTeacher().getEmail());
                map.put("teacherPhone", s.getTeacher().getPhone());
                map.put("isAssigned", true);
            } else {
                map.put("teacherId", null);
                map.put("teacherName", "Unassigned");
                map.put("teacherEmail", "—");
                map.put("teacherPhone", "—");
                map.put("isAssigned", false);
            }
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    // 3. Assign or Reassign Single Student to Teacher Mentor
    @PutMapping("/assign-mentor")
    @Transactional
    public ResponseEntity<Map<String, Object>> assignMentor(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("studentId")) {
            return ResponseEntity.badRequest().body(Map.of("error", "studentId is required"));
        }
        Integer studentId = Integer.valueOf(body.get("studentId").toString());
        StudentEntity student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        Integer teacherId = body.get("teacherId") != null ? Integer.valueOf(body.get("teacherId").toString()) : null;
        if (teacherId != null && teacherId > 0) {
            PrincipalEntity teacher = teacherRepo.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherId));
            student.setTeacher(teacher);
        } else {
            student.setTeacher(null);
        }

        studentRepo.save(student);

        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("studentId", student.getStudentid());
        res.put("studentName", student.getStudentname());
        res.put("assignedTeacherId", student.getTeacher() != null ? student.getTeacher().getTeacherid() : null);
        res.put("assignedTeacherName", student.getTeacher() != null ? student.getTeacher().getTeachername() : "Unassigned");
        res.put("message", "Mentor relationship updated successfully");
        return ResponseEntity.ok(res);
    }

    // 4. Bulk Assign Multiple Students to Teacher Mentor
    @PutMapping("/batch-assign-mentor")
    @Transactional
    public ResponseEntity<Map<String, Object>> batchAssignMentor(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("studentIds") || !body.containsKey("teacherId")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Both studentIds array and teacherId are required"));
        }

        Integer teacherId = Integer.valueOf(body.get("teacherId").toString());
        PrincipalEntity teacher = teacherId > 0 ? teacherRepo.findById(teacherId).orElse(null) : null;

        @SuppressWarnings("unchecked")
        List<Object> rawIds = (List<Object>) body.get("studentIds");
        int count = 0;

        for (Object rawId : rawIds) {
            Integer sId = Integer.valueOf(rawId.toString());
            Optional<StudentEntity> studentOpt = studentRepo.findById(sId);
            if (studentOpt.isPresent()) {
                StudentEntity s = studentOpt.get();
                s.setTeacher(teacher);
                studentRepo.save(s);
                count++;
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("assignedCount", count);
        res.put("teacherName", teacher != null ? teacher.getTeachername() : "Unassigned");
        res.put("message", count + " students successfully assigned to " + (teacher != null ? teacher.getTeachername() : "Unassigned"));
        return ResponseEntity.ok(res);
    }

    // 5. Faculty Workload & Cohort Distribution
    @GetMapping("/faculty-workload")
    public ResponseEntity<List<Map<String, Object>>> getFacultyWorkload() {
        List<PrincipalEntity> teachers = teacherRepo.findAll();
        List<Map<String, Object>> list = teachers.stream().map(t -> {
            List<StudentEntity> mentees = studentRepo.findByTeacher_Teacherid(t.getTeacherid());
            double cohortFees = mentees.stream().mapToDouble(StudentEntity::getFeebalance).sum();

            Map<String, Object> map = new HashMap<>();
            map.put("teacherId", t.getTeacherid());
            map.put("teacherName", t.getTeachername());
            map.put("email", t.getEmail());
            map.put("phone", t.getPhone());
            map.put("menteeCount", mentees.size());
            map.put("cohortTotalFeePending", Math.round(cohortFees * 100.0) / 100.0);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    // 6. Direct Fee Adjustment from Principal Control Center
    @PostMapping("/student/{id}/fee")
    public ResponseEntity<AddFeeUpdateResponseDTO> adjustStudentFee(
            @PathVariable Integer id,
            @RequestBody AddFeeUpdateRequestDTO request) {
        request.setStudentid(id);
        AddFeeUpdateResponseDTO response = feeUpdateService.setfeeToStudentById(request);
        return ResponseEntity.ok(response);
    }

    // 7. Recent Transactions Feed
    @GetMapping("/recent-transactions")
    public ResponseEntity<List<Map<String, Object>>> getRecentTransactions() {
        List<FeeHistory> list = feeHistoryRepo.findTop5ByOrderByFeepaymentIdDesc();
        List<Map<String, Object>> records = list.stream().map(fh -> {
            Map<String, Object> map = new HashMap<>();
            map.put("paymentId", fh.getFeepaymentId());
            map.put("amount", fh.getAmount());
            map.put("date", fh.getDate() != null ? fh.getDate().toString() : "");
            map.put("time", fh.getTime() != null ? fh.getTime().toString() : "");
            map.put("studentName", fh.getStudent() != null ? fh.getStudent().getStudentname() : "N/A");
            map.put("studentId", fh.getStudent() != null ? fh.getStudent().getStudentid() : null);
            map.put("teacherName", fh.getTeacher() != null ? fh.getTeacher().getTeachername() : "N/A");
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(records);
    }
}

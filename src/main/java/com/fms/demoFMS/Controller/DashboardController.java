package com.fms.demoFMS.Controller;

import com.fms.demoFMS.Entity.FeeHistory;
import com.fms.demoFMS.Repos.FeeHistoryRepo;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final StudentRepo studentRepo;
    private final TeacherRepo teacherRepo;
    private final FeeHistoryRepo feeHistoryRepo;

    public DashboardController(StudentRepo studentRepo, TeacherRepo teacherRepo, FeeHistoryRepo feeHistoryRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
        this.feeHistoryRepo = feeHistoryRepo;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalStudents = studentRepo.count();
        long totalTeachers = teacherRepo.count();
        Double totalFees = feeHistoryRepo.getTotalFeesCollected();
        long totalPayments = feeHistoryRepo.count();

        stats.put("totalStudents", totalStudents);
        stats.put("totalTeachers", totalTeachers);
        stats.put("totalFeesCollected", totalFees != null ? totalFees : 0.0);
        stats.put("totalPayments", totalPayments);
        stats.put("systemStatus", "Online");

        List<FeeHistory> recentHistory = feeHistoryRepo.findTop5ByOrderByFeepaymentIdDesc();
        List<Map<String, Object>> recentList = recentHistory.stream().map(fh -> {
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

        stats.put("recentTransactions", recentList);

        return ResponseEntity.ok(stats);
    }
}

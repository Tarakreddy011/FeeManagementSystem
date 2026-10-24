package com.fms.demoFMS.FeeService;

import com.fms.demoFMS.DTOS.FeeRecpitDTO.RequestFeePaymentDTO;
import com.fms.demoFMS.DTOS.FeeRecpitDTO.ResponseFeePaymentDTO;
import com.fms.demoFMS.Entity.FeeHistory;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.FeeHistoryRepo;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class FeeRecipt {

    private final StudentRepo studentRepo;
    private final TeacherRepo teacherRepo;
    private final FeeHistoryRepo feeHistoryRepo;

    @Autowired
    public FeeRecipt(StudentRepo studentRepo, TeacherRepo teacherRepo, FeeHistoryRepo feeHistoryRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
        this.feeHistoryRepo = feeHistoryRepo;
    }

    @Transactional
    public ResponseFeePaymentDTO feereciptgeneration(RequestFeePaymentDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null");
        }
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        if (request.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required for payment");
        }

        StudentEntity student = studentRepo.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getStudentId()));

        double currentBalance = student.getFeebalance();
        double paymentAmount = request.getAmount();

        if (paymentAmount > currentBalance) {
            throw new IllegalArgumentException("Payment amount of " + paymentAmount + 
                    " exceeds current outstanding balance of " + currentBalance);
        }

        double newBalance = Math.round((currentBalance - paymentAmount) * 100.0) / 100.0;
        student.setFeebalance(newBalance);
        studentRepo.save(student);

        // Resolve teacher: prefer request's teacherId, then student's assigned teacher, then first available teacher
        PrincipalEntity teacher = null;
        if (request.getTeacherId() != null && request.getTeacherId() > 0) {
            teacher = teacherRepo.findById(request.getTeacherId()).orElse(null);
        }
        if (teacher == null && student.getTeacher() != null) {
            teacher = student.getTeacher();
        }
        if (teacher == null) {
            teacher = teacherRepo.findAll().stream().findFirst().orElse(null);
        }

        FeeHistory feeHistory = new FeeHistory();
        feeHistory.setDate(LocalDate.now());
        feeHistory.setTime(LocalTime.now());
        feeHistory.setAmount(paymentAmount);
        feeHistory.setStudent(student);
        feeHistory.setTeacher(teacher);
        feeHistory = feeHistoryRepo.save(feeHistory);

        ResponseFeePaymentDTO response = new ResponseFeePaymentDTO();
        response.setNewfeebalanceafterpayment(newBalance);
        response.setAmount(paymentAmount);
        response.setStudentId(student.getStudentid());
        response.setTeacherId(teacher != null ? teacher.getTeacherid() : null);
        response.setFeepaymentId(feeHistory.getFeepaymentId());
        response.setDate(feeHistory.getDate());
        response.setTime(feeHistory.getTime());

        return response;
    }
}

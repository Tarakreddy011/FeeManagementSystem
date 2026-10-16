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

    public ResponseFeePaymentDTO feereciptgeneration(RequestFeePaymentDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null");
        }
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        StudentEntity student = studentRepo.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getStudentId()));

        PrincipalEntity teacher = teacherRepo.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + request.getTeacherId()));

        double newBalance = student.getFeebalance() - request.getAmount();
        student.setFeebalance(newBalance);
        studentRepo.save(student);

        FeeHistory feeHistory = new FeeHistory();
        feeHistory.setDate(LocalDate.now());
        feeHistory.setTime(LocalTime.now());
        feeHistory.setAmount(request.getAmount());
        feeHistory.setStudent(student);
        feeHistory.setTeacher(teacher);
        feeHistory = feeHistoryRepo.save(feeHistory);

        ResponseFeePaymentDTO response = new ResponseFeePaymentDTO();
        response.setNewfeebalanceafterpayment(newBalance);
        response.setAmount(request.getAmount());
        response.setStudentId(student.getStudentid());
        response.setTeacherId(teacher.getTeacherid());
        response.setFeepaymentId(feeHistory.getFeepaymentId());
        response.setDate(feeHistory.getDate());
        response.setTime(feeHistory.getTime());

        return response;
    }
}

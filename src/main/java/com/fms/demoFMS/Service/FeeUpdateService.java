package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddFee.AddFeeUpdateRequestDTO;
import com.fms.demoFMS.DTOS.AddFee.AddFeeUpdateResponseDTO;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeeUpdateService {

    private final StudentRepo studentRepo;
    private final TeacherRepo teacherRepo;

    @Autowired
    public FeeUpdateService(StudentRepo studentRepo, TeacherRepo teacherRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
    }

    @Transactional
    public AddFeeUpdateResponseDTO setfeeToStudentById(AddFeeUpdateRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Fee update request cannot be null");
        }
        if (request.getStudentid() == null || request.getStudentid() <= 0) {
            throw new IllegalArgumentException("Student ID is required and must be valid");
        }
        if (!Double.isFinite(request.getFeebalance()) || request.getFeebalance() < 0) {
            throw new IllegalArgumentException("Fee balance must be finite and cannot be negative");
        }

        StudentEntity student = studentRepo.findById(request.getStudentid())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getStudentid()));

        // Resolve teacher: prefer request's teacherId, otherwise preserve student's existing teacher
        PrincipalEntity teacher = student.getTeacher();
        if (request.getTeacherid() != null && request.getTeacherid() > 0) {
            teacher = teacherRepo.findById(request.getTeacherid())
                    .orElse(teacher);
        }

        double roundedFee = Math.round(request.getFeebalance() * 100.0) / 100.0;
        student.setFeebalance(roundedFee);
        if (teacher != null) {
            student.setTeacher(teacher);
        }
        studentRepo.save(student);

        AddFeeUpdateResponseDTO response = new AddFeeUpdateResponseDTO();
        response.setNewfee(roundedFee);
        response.setStudentid(student.getStudentid());
        response.setFatherName(student.getFathername());
        response.setStudentname(student.getStudentname());
        response.setEmail(student.getEmail());
        response.setFeebalance(roundedFee);

        if (teacher != null) {
            response.setTeacherName(teacher.getTeachername());
            response.setTeaherid(teacher.getTeacherid());
            response.setTeacherid(teacher.getTeacherid());
        }

        return response;
    }
}

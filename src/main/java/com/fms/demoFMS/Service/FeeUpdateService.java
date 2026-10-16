package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddFee.AddFeeUpdateRequestDTO;
import com.fms.demoFMS.DTOS.AddFee.AddFeeUpdateResponseDTO;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeeUpdateService {

    private final StudentRepo studentRepo;
    private final TeacherRepo teacherRepo;

    @Autowired
    public FeeUpdateService(StudentRepo studentRepo, TeacherRepo teacherRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
    }

    public AddFeeUpdateResponseDTO setfeeToStudentById(AddFeeUpdateRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Fee update request cannot be null");
        }
        if (request.getFeebalance() < 0) {
            throw new IllegalArgumentException("Fee balance cannot be negative");
        }

        StudentEntity student = studentRepo.findById(request.getStudentid())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getStudentid()));

        PrincipalEntity teacher = teacherRepo.findById(request.getTeacherid())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + request.getTeacherid()));

        student.setFeebalance(request.getFeebalance());
        student.setTeacher(teacher);
        studentRepo.save(student);

        AddFeeUpdateResponseDTO response = new AddFeeUpdateResponseDTO();
        response.setNewfee(request.getFeebalance());
        response.setStudentid(student.getStudentid());
        response.setFatherName(student.getFathername());
        response.setTeacherName(teacher.getTeachername());
        response.setTeaherid(teacher.getTeacherid());
        response.setFeebalance(request.getFeebalance());
        response.setStudentname(student.getStudentname());
        response.setEmail(student.getEmail());

        return response;
    }
}

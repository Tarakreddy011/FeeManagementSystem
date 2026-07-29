package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddFeeUpdateResponseDTO;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;

public class FeeUpdateService {

    StudentRepo studentRepo;
    TeacherRepo teacherRepo;

    @Autowired
    public  FeeUpdateService(StudentRepo studentRepo, TeacherRepo teacherRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
    }

    public AddFeeUpdateResponseDTO setfeeToStudentById(AddFeeUpdateResponseDTO request) {
        StudentEntity student = studentRepo.findById(request.getStudentid())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        PrincipalEntity teacher = teacherRepo.findById(request.getTeaherid())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        student.setFeebalance(request.getFeebalance());
        student.setTeacher(teacher);

        studentRepo.save(student);

        AddFeeUpdateResponseDTO response = new AddFeeUpdateResponseDTO();
        response.setStudentid(student.getStudentid());
        response.setTeaherid(teacher.getTeacherid());
        response.setFeebalance(request.getFeebalance());
        response.setTeachername(request.getTeachername());
        response.setStudentname(student.getStudentname());
        response.setNewfee(request.getNewfee());
        response.setFatherName(request.getFatherName());
        return response;
    }
}

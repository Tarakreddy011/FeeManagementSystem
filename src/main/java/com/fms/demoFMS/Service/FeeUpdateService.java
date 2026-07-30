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

    StudentRepo studentRepo;
    TeacherRepo teacherRepo;

    @Autowired
    public  FeeUpdateService(StudentRepo studentRepo, TeacherRepo teacherRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
    }

    public AddFeeUpdateResponseDTO setfeeToStudentById(AddFeeUpdateRequestDTO request) {
        StudentEntity student = studentRepo.findById(request.getStudentid())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        PrincipalEntity teacher = teacherRepo.findById(request.getTeacherid())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        student.setFeebalance(request.getFeebalance());
        student.setTeacher(teacher);


        studentRepo.save(student);

        AddFeeUpdateResponseDTO response = new AddFeeUpdateResponseDTO();
        response.setNewfee(request.getFeebalance());
        response.setStudentid(student.getStudentid());
        response.setFatherName(student.getFatherName());
        response.setTeacherName(teacher.getTeachername());
        response.setTeaherid(teacher.getTeacherid());
        response.setFeebalance(request.getFeebalance());
        response.setStudentname(student.getStudentname());
        //response.setNewfee(request.());      //need to add thew clculated new fees
        //response.setNewfee(request.getNewfee());
        response.setEmail(student.getEmail());

        return response;
    }
}

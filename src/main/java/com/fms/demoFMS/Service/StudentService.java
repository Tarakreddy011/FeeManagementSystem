package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddStudentdto;
import com.fms.demoFMS.DTOS.AddTeacherdto;
import com.fms.demoFMS.DTOS.ResponseStudentDto;
import com.fms.demoFMS.DTOS.ResponseTeacherdto;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    StudentRepo studentRepo;

    @Autowired
    public StudentService(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    public ResponseStudentDto addstudent(AddStudentdto addStudentdto) {
//        ResponseStudentDto responseStudentDto = new ResponseStudentDto();
        StudentEntity studentEntity = mapToStudent(addStudentdto);
        studentRepo.save(studentEntity);
        return mapToResponseStudent(studentEntity);
    }

    public ResponseStudentDto mapToResponseStudent(StudentEntity studentEntity) {
        ResponseStudentDto responseStudentDto = new ResponseStudentDto();
        responseStudentDto.setId(studentEntity.getId());
        responseStudentDto.setEmail(studentEntity.getEmail());
        responseStudentDto.setFatherName(studentEntity.getFatherName());
        responseStudentDto.setFeebalance(studentEntity.getFeebalance());
        return responseStudentDto;
    }




    public StudentEntity mapToStudent(AddStudentdto addStudentdto) {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setId(addStudentdto.getId());
        studentEntity.setEmail(addStudentdto.getEmail());
        studentEntity.setFatherName(addStudentdto.getFatherName());
        studentEntity.setFeebalance(addStudentdto.getFeebalance());
        return studentEntity;
    }

}
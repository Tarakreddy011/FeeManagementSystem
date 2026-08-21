package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddEntity.AddStudentdto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseStudentDto;
import com.fms.demoFMS.DTOS.UpdateEntity.StudentUpdate;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.boot.io.ApplicationResourceLoader.get;

@Service
public class StudentService {

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    public StudentService(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    public ResponseStudentDto addstudent(AddStudentdto addStudentdto) {
        StudentEntity studentEntity = mapToStudent(addStudentdto);
        if(addStudentdto.getFeebalance() <= 0){
            return null;
        }
        studentRepo.save(studentEntity);
        return mapToResponseStudent(studentEntity);
    }

    public ResponseStudentDto getstudentById(Integer id) {
        StudentEntity studentEntity = studentRepo.findById(id).get();
        return mapToResponseStudent(studentEntity);
    }

//    public StudentEntity getstudentByname(String studentname) {
//        return studentRepo.findByName(studentname)
//                .orElseThrow(() -> new RuntimeException("Student not found with name: " + studentname));
//    }
public StudentEntity getstudentByname(String studentname) {
    return studentRepo.findByStudentname(studentname)
            .orElseThrow(() -> new RuntimeException("Student not found with name: " + studentname));
}



    @Test
    public ResponseStudentDto updateStudentByID(Integer id, StudentUpdate studentUpdate) {
        StudentEntity existingStudentEntity = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student Not found"));

        existingStudentEntity.setStudentname(studentUpdate.getStudentname());
        existingStudentEntity.setFathername(studentUpdate.getFatherName());

        StudentEntity updatedStudentEntity = studentRepo.save(existingStudentEntity);
        return mapToResponseStudent(updatedStudentEntity);
    }

    public StudentEntity deleteStudentByID(Integer id) {
        StudentEntity reslut = studentRepo.findById(id).get();
        studentRepo.delete(reslut);
        return reslut;
    }

    public ResponseStudentDto mapToResponseStudent(StudentEntity studentEntity) {
        ResponseStudentDto responseStudentDto = new ResponseStudentDto();
        responseStudentDto.setStudentid(studentEntity.getStudentid());
        responseStudentDto.setEmail(studentEntity.getEmail());
        responseStudentDto.setStudentname(studentEntity.getStudentname());
        responseStudentDto.setFathername(studentEntity.getFathername());
        responseStudentDto.setFeebalance(studentEntity.getFeebalance());
        return responseStudentDto;
    }

    public StudentEntity mapToStudent(AddStudentdto addStudentdto) {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setStudentid(addStudentdto.getId());
        studentEntity.setEmail(addStudentdto.getEmail());
        studentEntity.setStudentname(addStudentdto.getStudentname());
        studentEntity.setFathername(addStudentdto.getFathername());
        studentEntity.setFeebalance(addStudentdto.getFeebalance());
        if(addStudentdto.getFeebalance()<= 0){
            throw new IllegalArgumentException("Feebalance cannot be negative");
        }
        return studentEntity;
    }



}
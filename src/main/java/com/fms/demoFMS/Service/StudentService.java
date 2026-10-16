package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddEntity.AddStudentdto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseStudentDto;
import com.fms.demoFMS.DTOS.UpdateEntity.StudentUpdate;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class StudentService {

    private final StudentRepo studentRepo;

    @Autowired
    public StudentService(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    public ResponseStudentDto addstudent(AddStudentdto addStudentdto) {
        if (addStudentdto.getFeebalance() <= 0) {
            throw new IllegalArgumentException("Fee balance must be greater than zero");
        }
        StudentEntity studentEntity = mapToStudent(addStudentdto);

        if (addStudentdto.getBirthdate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String pwd = addStudentdto.getBirthdate().format(formatter).replaceAll("[^0-9]", "");
            studentEntity.setPassword(pwd);
        } else {
            studentEntity.setPassword("student123");
        }

        studentRepo.save(studentEntity);
        return mapToResponseStudent(studentEntity);
    }

    public ResponseStudentDto getstudentById(Integer id) {
        StudentEntity studentEntity = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
        return mapToResponseStudent(studentEntity);
    }

    public java.util.List<ResponseStudentDto> getAllStudents() {
        return studentRepo.findAll().stream()
                .map(this::mapToResponseStudent)
                .collect(java.util.stream.Collectors.toList());
    }

    public StudentEntity getstudentByname(String studentname) {
        return studentRepo.findByStudentname(studentname)
                .orElseThrow(() -> new RuntimeException("Student not found with name: " + studentname));
    }

    public ResponseStudentDto updateStudentByID(Integer id, StudentUpdate studentUpdate) {
        StudentEntity existingStudentEntity = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        if (studentUpdate != null) {
            if (studentUpdate.getStudentname() != null && !studentUpdate.getStudentname().isBlank()) {
                existingStudentEntity.setStudentname(studentUpdate.getStudentname());
            }
            if (studentUpdate.getFatherName() != null && !studentUpdate.getFatherName().isBlank()) {
                existingStudentEntity.setFathername(studentUpdate.getFatherName());
            }
        }

        StudentEntity updatedStudentEntity = studentRepo.save(existingStudentEntity);
        return mapToResponseStudent(updatedStudentEntity);
    }

    public StudentEntity deleteStudentByID(Integer id) {
        StudentEntity student = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
        studentRepo.delete(student);
        return student;
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
        studentEntity.setBirthdate(addStudentdto.getBirthdate());
        studentEntity.setFathername(addStudentdto.getFathername());
        studentEntity.setFeebalance(addStudentdto.getFeebalance());
        return studentEntity;
    }
}
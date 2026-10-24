package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddEntity.AddStudentdto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseStudentDto;
import com.fms.demoFMS.DTOS.UpdateEntity.StudentUpdate;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepo studentRepo;
    private final TeacherRepo teacherRepo;

    @Autowired
    public StudentService(StudentRepo studentRepo, TeacherRepo teacherRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
    }

    @Transactional
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

        if (addStudentdto.getTeacherId() != null && addStudentdto.getTeacherId() > 0) {
            PrincipalEntity teacher = teacherRepo.findById(addStudentdto.getTeacherId()).orElse(null);
            studentEntity.setTeacher(teacher);
        }

        studentRepo.save(studentEntity);
        return mapToResponseStudent(studentEntity);
    }

    public ResponseStudentDto getstudentById(Integer id) {
        StudentEntity studentEntity = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
        return mapToResponseStudent(studentEntity);
    }

    public List<ResponseStudentDto> getAllStudents() {
        return studentRepo.findAll().stream()
                .map(this::mapToResponseStudent)
                .collect(Collectors.toList());
    }

    public StudentEntity getstudentByname(String studentname) {
        return studentRepo.findByStudentnameIgnoreCase(studentname)
                .or(() -> studentRepo.findByStudentname(studentname))
                .orElseThrow(() -> new RuntimeException("Student not found with name: " + studentname));
    }

    public StudentEntity getStudentByEmailOrUsername(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier cannot be empty");
        }
        String cleanIdentifier = identifier.trim();
        return studentRepo.findByEmailIgnoreCase(cleanIdentifier)
                .or(() -> studentRepo.findByStudentnameIgnoreCase(cleanIdentifier))
                .or(() -> studentRepo.findByEmail(cleanIdentifier))
                .or(() -> studentRepo.findByStudentname(cleanIdentifier))
                .orElseThrow(() -> new RuntimeException("Student not found with identifier: " + identifier));
    }

    @Transactional
    public ResponseStudentDto updateStudentByID(Integer id, StudentUpdate studentUpdate) {
        StudentEntity existingStudentEntity = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        if (studentUpdate != null) {
            if (studentUpdate.getStudentname() != null && !studentUpdate.getStudentname().isBlank()) {
                existingStudentEntity.setStudentname(studentUpdate.getStudentname().trim());
            }
            if (studentUpdate.getFatherName() != null && !studentUpdate.getFatherName().isBlank()) {
                existingStudentEntity.setFathername(studentUpdate.getFatherName().trim());
            }
            if (studentUpdate.getEmail() != null && !studentUpdate.getEmail().isBlank()) {
                existingStudentEntity.setEmail(studentUpdate.getEmail().trim());
            }
            if (studentUpdate.getFeebalance() != null) {
                double roundedFee = Math.round(studentUpdate.getFeebalance() * 100.0) / 100.0;
                existingStudentEntity.setFeebalance(roundedFee);
            }
            if (studentUpdate.getTeacherId() != null) {
                if (studentUpdate.getTeacherId() > 0) {
                    PrincipalEntity teacher = teacherRepo.findById(studentUpdate.getTeacherId())
                            .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + studentUpdate.getTeacherId()));
                    existingStudentEntity.setTeacher(teacher);
                } else {
                    existingStudentEntity.setTeacher(null);
                }
            }
        }

        StudentEntity updatedStudentEntity = studentRepo.save(existingStudentEntity);
        return mapToResponseStudent(updatedStudentEntity);
    }

    @Transactional
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
        responseStudentDto.setBirthdate(studentEntity.getBirthdate());

        if (studentEntity.getTeacher() != null) {
            responseStudentDto.setTeacherId(studentEntity.getTeacher().getTeacherid());
            responseStudentDto.setTeacherName(studentEntity.getTeacher().getTeachername());
            responseStudentDto.setTeacherEmail(studentEntity.getTeacher().getEmail());
            responseStudentDto.setTeacherPhone(studentEntity.getTeacher().getPhone());
        }
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

package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddEntity.AddTeacherdto;
import com.fms.demoFMS.DTOS.AddEntity.ResponseTeacherdto;
import com.fms.demoFMS.DTOS.UpdateEntity.TeacherUpdate;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepo teacherRepo;

    @Autowired
    public TeacherService(TeacherRepo teacherRepo) {
        this.teacherRepo = teacherRepo;
    }

    public ResponseTeacherdto addTeacher(AddTeacherdto addTeacherdto) {
        PrincipalEntity principalEntity = mapToPrincipal(addTeacherdto);

        if (addTeacherdto.getBirthdate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            String pwd = addTeacherdto.getBirthdate().format(formatter).replaceAll("[^0-9]", "");
            principalEntity.setPassword(pwd);
        } else {
            principalEntity.setPassword("teacher123");
        }

        teacherRepo.save(principalEntity);
        return maptoResponseTeacherdto(principalEntity);
    }

    public ResponseTeacherdto getTeacherById(Integer id) {
        PrincipalEntity teacher = teacherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));
        return maptoResponseTeacherdto(teacher);
    }

    public List<ResponseTeacherdto> getAllTeachers() {
        return teacherRepo.findAll().stream()
                .map(this::maptoResponseTeacherdto)
                .collect(Collectors.toList());
    }

    public ResponseTeacherdto updateTeacherinfo(Integer id, TeacherUpdate teacherUpdate) {
        PrincipalEntity existingEntity = teacherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));

        if (teacherUpdate != null) {
            if (teacherUpdate.getTeachername() != null && !teacherUpdate.getTeachername().isBlank()) {
                existingEntity.setTeachername(teacherUpdate.getTeachername());
            }
            if (teacherUpdate.getPhone() > 0) {
                existingEntity.setPhone(teacherUpdate.getPhone());
            }
        }

        PrincipalEntity updatedEntity = teacherRepo.save(existingEntity);
        return maptoResponseTeacherdto(updatedEntity);
    }

    public PrincipalEntity deleteTeacher(Integer id) {
        PrincipalEntity result = teacherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));
        teacherRepo.delete(result);
        return result;
    }

    public PrincipalEntity getTeacherByName(String teachername) {
        return teacherRepo.findByTeachername(teachername)
                .orElseThrow(() -> new RuntimeException("Teacher not found with name: " + teachername));
    }

    public PrincipalEntity mapToPrincipal(AddTeacherdto addTeacherdto) {
        PrincipalEntity principalEntity = new PrincipalEntity();
        principalEntity.setTeacherid(addTeacherdto.getId());
        principalEntity.setEmail(addTeacherdto.getEmail());
        principalEntity.setPhone(addTeacherdto.getPhone());
        principalEntity.setBirthdate(addTeacherdto.getBirthdate());
        principalEntity.setTeachername(addTeacherdto.getTeachername());
        return principalEntity;
    }

    public ResponseTeacherdto maptoResponseTeacherdto(PrincipalEntity principalEntity) {
        ResponseTeacherdto responseTeacherdto = new ResponseTeacherdto();
        responseTeacherdto.setTeacherid(principalEntity.getTeacherid());
        responseTeacherdto.setEmail(principalEntity.getEmail());
        responseTeacherdto.setPhone(principalEntity.getPhone());
        responseTeacherdto.setTeachername(principalEntity.getTeachername());
        return responseTeacherdto;
    }
}

package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddTeacherdto;
import com.fms.demoFMS.DTOS.ResponseTeacherdto;
import com.fms.demoFMS.DTOS.TeacherUpdate;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {


    TeacherRepo teacherRepo;
    @Autowired
    public TeacherService(TeacherRepo teacherRepo) {
        this.teacherRepo = teacherRepo;
    }

    public ResponseTeacherdto addTeacher(AddTeacherdto addTeacherdto) {
        PrincipalEntity principalEntity = mapToPrincipal(addTeacherdto);

        teacherRepo.save(principalEntity);
        return maptoResponseTeacherdto(principalEntity);
    }

    public ResponseTeacherdto getTeacherById(Integer id) {
        ResponseTeacherdto responseTeacherdto = new ResponseTeacherdto();
        return  maptoResponseTeacherdto(teacherRepo.findById(id).get());
    }

    public ResponseTeacherdto updateTeacherinfo(Integer id, TeacherUpdate teacherUpdate) {
        PrincipalEntity existingEntity = teacherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        existingEntity.setTeachername(teacherUpdate.getTeachername());
        existingEntity.setPhone(teacherUpdate.getPhone());
        PrincipalEntity updatedEntity = teacherRepo.save(existingEntity);
        return maptoResponseTeacherdto(updatedEntity);
    }

    public PrincipalEntity deleteTeacher(Integer id) {
        PrincipalEntity result = teacherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        teacherRepo.delete(result);
        return result;
    }

    public PrincipalEntity mapToPrincipal(AddTeacherdto addTeacherdto) {
        PrincipalEntity principalEntity = new PrincipalEntity();
        //principalEntity.setId(addTeacherdto.getId());
        principalEntity.setEmail(addTeacherdto.getEmail());
        principalEntity.setPhone(addTeacherdto.getPhone());
        principalEntity.setTeachername(addTeacherdto.getTeachername());
        return principalEntity;
    }

    public ResponseTeacherdto maptoResponseTeacherdto(PrincipalEntity principalEntity) {
        ResponseTeacherdto responseTeacherdto = new ResponseTeacherdto();
        //responseTeacherdto.setId(principalEntity.getId());
        responseTeacherdto.setEmail(principalEntity.getEmail());
        responseTeacherdto.setPhone(principalEntity.getPhone());
        responseTeacherdto.setTeachername(principalEntity.getTeachername());
        return responseTeacherdto;
    }

}

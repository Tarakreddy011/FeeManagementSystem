package com.fms.demoFMS.Repos;

import com.fms.demoFMS.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<StudentEntity, Integer> {

    Optional<StudentEntity> findByStudentname(String studentname);
    Optional<StudentEntity> findByStudentnameIgnoreCase(String studentname);
    Optional<StudentEntity> findByEmail(String email);
    Optional<StudentEntity> findByEmailIgnoreCase(String email);

    List<StudentEntity> findByTeacher_Teacherid(Integer teacherid);
    boolean existsByTeacher_Teacherid(Integer teacherid);
}

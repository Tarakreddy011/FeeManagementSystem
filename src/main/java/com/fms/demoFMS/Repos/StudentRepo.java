package com.fms.demoFMS.Repos;

import com.fms.demoFMS.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface StudentRepo extends JpaRepository<StudentEntity, Integer> {

    //Optional<StudentEntity> findByName(String studentname);
    Optional<StudentEntity> findByStudentname(String studentname);




    /*@Query("SELECT s FROM StudentEntity s WHERE s.studentName = :name")
    Optional<StudentEntity> findByName(@Param("name") String name);*/

}

package com.fms.demoFMS.Repos;

import com.fms.demoFMS.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentRepo extends JpaRepository<StudentEntity, Integer> {
}

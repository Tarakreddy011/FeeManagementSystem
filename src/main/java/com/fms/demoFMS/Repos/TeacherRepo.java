package com.fms.demoFMS.Repos;

import com.fms.demoFMS.Entity.PrincipalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TeacherRepo extends JpaRepository<PrincipalEntity, Integer> {
    Optional<PrincipalEntity> findByTeachername(String teachername);
}


package com.fms.demoFMS.Repos;

import com.fms.demoFMS.Entity.Principal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrincipalRepo extends JpaRepository<Principal, Integer> {
    Optional<Principal> findByUsername(String username);
    Optional<Principal> findByUsernameIgnoreCase(String username);
    Optional<Principal> findByEmail(String email);
    Optional<Principal> findByEmailIgnoreCase(String email);
}

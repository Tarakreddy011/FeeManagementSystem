package com.fms.demoFMS.config;

import com.fms.demoFMS.Entity.Principal;
import com.fms.demoFMS.Repos.PrincipalRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PrincipalRepo principalRepo;

    public DataInitializer(PrincipalRepo principalRepo) {
        this.principalRepo = principalRepo;
    }

    @Override
    public void run(String... args) {
        if (principalRepo.count() == 0) {
            Principal principal = new Principal();
            principal.setName("Dr. Principal");
            principal.setUsername("principal");
            principal.setEmail("principal@paymyfee.edu");
            principal.setPassword("principal123");
            principal.setPhone("9876543210");
            principal.setRole("ROLE_ADMIN");
            principalRepo.save(principal);
        }
    }
}

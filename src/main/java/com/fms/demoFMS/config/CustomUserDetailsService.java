package com.fms.demoFMS.config;

import com.fms.demoFMS.Entity.Principal;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.PrincipalRepo;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PrincipalRepo principalRepo;
    private final TeacherRepo teacherRepo;
    private final StudentRepo studentRepo;

    @Value("${spring.security.user.name:user}")
    private String defaultUserName;

    @Value("${spring.security.user.password:pwd}")
    private String defaultUserPassword;

    public CustomUserDetailsService(PrincipalRepo principalRepo, TeacherRepo teacherRepo, StudentRepo studentRepo) {
        this.principalRepo = principalRepo;
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Username cannot be empty");
        }
        String cleanUser = username.trim();

        // 1. Check default admin / system user configured in application.properties
        if (cleanUser.equalsIgnoreCase(defaultUserName)) {
            return new User(
                    defaultUserName,
                    defaultUserPassword,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // 2. Built-in administrative accounts
        if (cleanUser.equalsIgnoreCase("admin")) {
            return new User(
                    "admin",
                    "admin123",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
        if (cleanUser.equalsIgnoreCase("principal")) {
            return new User(
                    "principal",
                    "principal123",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
        if (cleanUser.equalsIgnoreCase("teacher")) {
            return new User(
                    "teacher",
                    "teacher123",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"))
            );
        }
        if (cleanUser.equalsIgnoreCase("student")) {
            return new User(
                    "student",
                    "student123",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
            );
        }

        // 3. Check Principal / Admin table
        Optional<Principal> principalOpt = principalRepo.findByEmailIgnoreCase(cleanUser)
                .or(() -> principalRepo.findByUsernameIgnoreCase(cleanUser));
        if (principalOpt.isPresent()) {
            Principal principal = principalOpt.get();
            String rawPwd = principal.getPassword() != null ? principal.getPassword() : "admin123";
            String cleanPwd = rawPwd.replaceAll("[^0-9a-zA-Z]", "");
            return new User(
                    principal.getEmail() != null ? principal.getEmail() : principal.getUsername(),
                    cleanPwd.isEmpty() ? rawPwd : cleanPwd,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // 4. Check Teacher table
        Optional<PrincipalEntity> teacherOpt = teacherRepo.findByEmailIgnoreCase(cleanUser)
                .or(() -> teacherRepo.findByTeachernameIgnoreCase(cleanUser))
                .or(() -> teacherRepo.findByEmail(cleanUser))
                .or(() -> teacherRepo.findByTeachername(cleanUser));
        if (teacherOpt.isPresent()) {
            PrincipalEntity teacher = teacherOpt.get();
            String rawPwd = teacher.getPassword() != null ? teacher.getPassword() : "";
            String cleanPwd = rawPwd.replaceAll("[^0-9a-zA-Z]", "");

            return new User(
                    teacher.getEmail() != null ? teacher.getEmail() : teacher.getTeachername(),
                    cleanPwd.isEmpty() ? rawPwd : cleanPwd,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"))
            );
        }

        // 5. Check Student table
        Optional<StudentEntity> studentOpt = studentRepo.findByEmailIgnoreCase(cleanUser)
                .or(() -> studentRepo.findByStudentnameIgnoreCase(cleanUser))
                .or(() -> studentRepo.findByEmail(cleanUser))
                .or(() -> studentRepo.findByStudentname(cleanUser));
        if (studentOpt.isPresent()) {
            StudentEntity student = studentOpt.get();
            String rawPwd = student.getPassword() != null ? student.getPassword() : "";
            String cleanPwd = rawPwd.replaceAll("[^0-9a-zA-Z]", "");

            return new User(
                    student.getEmail() != null ? student.getEmail() : student.getStudentname(),
                    cleanPwd.isEmpty() ? rawPwd : cleanPwd,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
            );
        }

        throw new UsernameNotFoundException("User not found with username/email: " + username);
    }
}

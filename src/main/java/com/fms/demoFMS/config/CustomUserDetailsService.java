package com.fms.demoFMS.config;

import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
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

    private final TeacherRepo teacherRepo;
    private final StudentRepo studentRepo;

    @Value("${spring.security.user.name:user}")
    private String defaultUserName;

    @Value("${spring.security.user.password:pwd}")
    private String defaultUserPassword;

    public CustomUserDetailsService(TeacherRepo teacherRepo, StudentRepo studentRepo) {
        this.teacherRepo = teacherRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Check default admin / system user configured in application.properties
        if (username.equalsIgnoreCase(defaultUserName)) {
            return new User(
                    defaultUserName,
                    defaultUserPassword,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // 2. Built-in fallback administrative accounts for convenience
        if (username.equalsIgnoreCase("admin")) {
            return new User(
                    "admin",
                    "admin123",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }
        if (username.equalsIgnoreCase("teacher")) {
            return new User(
                    "teacher",
                    "teacher123",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"))
            );
        }
        if (username.equalsIgnoreCase("student")) {
            return new User(
                    "student",
                    "student123",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
            );
        }

        // 3. Check Teacher / Principal table by email or teachername
        Optional<PrincipalEntity> teacherOpt = teacherRepo.findByEmail(username);
        if (teacherOpt.isEmpty()) {
            teacherOpt = teacherRepo.findByTeachername(username);
        }
        if (teacherOpt.isPresent()) {
            PrincipalEntity teacher = teacherOpt.get();
            boolean isPrincipal = "true".equalsIgnoreCase(teacher.getIsprincipal())
                    || "yes".equalsIgnoreCase(teacher.getIsprincipal())
                    || "1".equals(teacher.getIsprincipal());

            String role = isPrincipal ? "ROLE_ADMIN" : "ROLE_TEACHER";
            String rawPwd = teacher.getPassword() != null ? teacher.getPassword() : "";
            // Strip any '-' or non-alphanumeric characters for clean comparison
            String cleanPwd = rawPwd.replaceAll("[^0-9a-zA-Z]", "");

            return new User(
                    teacher.getEmail() != null ? teacher.getEmail() : teacher.getTeachername(),
                    cleanPwd.isEmpty() ? rawPwd : cleanPwd,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
        }

        // 4. Check Student table by email or studentname
        Optional<StudentEntity> studentOpt = studentRepo.findByEmail(username);
        if (studentOpt.isEmpty()) {
            studentOpt = studentRepo.findByStudentname(username);
        }
        if (studentOpt.isPresent()) {
            StudentEntity student = studentOpt.get();
            String rawPwd = student.getPassword() != null ? student.getPassword() : "";
            // Strip any '-' or non-alphanumeric characters for clean comparison
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

package com.fms.demoFMS.Controller;

import com.fms.demoFMS.Entity.Principal;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.PrincipalRepo;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final StudentRepo studentRepo;
    private final TeacherRepo teacherRepo;
    private final PrincipalRepo principalRepo;

    public AuthController(StudentRepo studentRepo, TeacherRepo teacherRepo, PrincipalRepo principalRepo) {
        this.studentRepo = studentRepo;
        this.teacherRepo = teacherRepo;
        this.principalRepo = principalRepo;
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> login(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String username = authentication.getName();
            response.put("status", "success");
            response.put("username", username);
            response.put("roles", roles);

            String primaryRole = "STUDENT";
            if (roles.contains("ROLE_ADMIN")) {
                primaryRole = "ADMIN";
            } else if (roles.contains("ROLE_TEACHER")) {
                primaryRole = "TEACHER";
            }
            response.put("role", primaryRole);

            // Populate user details based on role
            if ("STUDENT".equals(primaryRole)) {
                Optional<StudentEntity> studentOpt = studentRepo.findByEmailIgnoreCase(username)
                        .or(() -> studentRepo.findByStudentnameIgnoreCase(username))
                        .or(() -> studentRepo.findByEmail(username))
                        .or(() -> studentRepo.findByStudentname(username));
                if (studentOpt.isPresent()) {
                    StudentEntity student = studentOpt.get();
                    response.put("userId", student.getStudentid());
                    response.put("displayName", student.getStudentname());
                    response.put("email", student.getEmail());
                    response.put("feebalance", student.getFeebalance());
                } else {
                    response.put("displayName", username);
                }
            } else if ("ADMIN".equals(primaryRole)) {
                Optional<Principal> principalOpt = principalRepo.findByEmailIgnoreCase(username)
                        .or(() -> principalRepo.findByUsernameIgnoreCase(username));
                if (principalOpt.isPresent()) {
                    Principal p = principalOpt.get();
                    response.put("userId", p.getPrincipalId());
                    response.put("displayName", p.getName() != null ? p.getName() : p.getUsername());
                    response.put("email", p.getEmail());
                } else {
                    response.put("displayName", "Principal");
                }
            } else if ("TEACHER".equals(primaryRole)) {
                Optional<PrincipalEntity> teacherOpt = teacherRepo.findByEmailIgnoreCase(username)
                        .or(() -> teacherRepo.findByTeachernameIgnoreCase(username))
                        .or(() -> teacherRepo.findByEmail(username))
                        .or(() -> teacherRepo.findByTeachername(username));
                if (teacherOpt.isPresent()) {
                    PrincipalEntity teacher = teacherOpt.get();
                    response.put("userId", teacher.getTeacherid());
                    response.put("displayName", teacher.getTeachername());
                    response.put("email", teacher.getEmail());
                } else {
                    response.put("displayName", username);
                }
            }

            response.put("message", "User authenticated successfully");
            return ResponseEntity.ok(response);
        }

        response.put("status", "error");
        response.put("message", "Not authenticated");
        return ResponseEntity.status(401).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        return login(authentication);
    }
}

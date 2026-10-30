package com.fms.demoFMS.Service;

import com.fms.demoFMS.DTOS.AddEntity.AddStudentdto;
import com.fms.demoFMS.DTOS.UpdateEntity.StudentUpdate;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private TeacherRepo teacherRepo;

    @InjectMocks
    private StudentService studentService;

    @Test
    void addStudentForTeacherAssignsTeacherAndInitialFee() {
        PrincipalEntity teacher = new PrincipalEntity();
        teacher.setTeacherid(12);
        teacher.setTeachername("Faculty member");
        AddStudentdto request = new AddStudentdto();
        request.setStudentname("New student");
        request.setFathername("Parent");
        request.setEmail("student@example.edu");
        request.setBirthdate(LocalDate.of(2010, 3, 14));
        request.setFeebalance(1250.50);
        when(studentRepo.save(any(StudentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        studentService.addStudentForTeacher(request, teacher);

        ArgumentCaptor<StudentEntity> savedStudent = ArgumentCaptor.forClass(StudentEntity.class);
        verify(studentRepo).save(savedStudent.capture());
        assertEquals(teacher, savedStudent.getValue().getTeacher());
        assertEquals(1250.50, savedStudent.getValue().getFeebalance());
        assertEquals("14032010", savedStudent.getValue().getPassword());
        verifyNoInteractions(teacherRepo);
    }

    @Test
    void addStudentForTeacherRejectsMissingInitialFee() {
        PrincipalEntity teacher = new PrincipalEntity();
        AddStudentdto request = new AddStudentdto();
        request.setFeebalance(0);

        assertThrows(IllegalArgumentException.class, () -> studentService.addStudentForTeacher(request, teacher));

        verifyNoInteractions(studentRepo, teacherRepo);
    }

    @Test
    void addStudentForTeacherRoundsInitialFeeToCents() {
        PrincipalEntity teacher = new PrincipalEntity();
        AddStudentdto request = new AddStudentdto();
        request.setFeebalance(12.345);
        when(studentRepo.save(any(StudentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        studentService.addStudentForTeacher(request, teacher);

        ArgumentCaptor<StudentEntity> savedStudent = ArgumentCaptor.forClass(StudentEntity.class);
        verify(studentRepo).save(savedStudent.capture());
        assertEquals(12.35, savedStudent.getValue().getFeebalance());
    }

    @Test
    void addStudentForTeacherRejectsInitialFeeThatRoundsToZero() {
        PrincipalEntity teacher = new PrincipalEntity();
        AddStudentdto request = new AddStudentdto();
        request.setFeebalance(0.004);

        assertThrows(IllegalArgumentException.class, () -> studentService.addStudentForTeacher(request, teacher));

        verifyNoInteractions(studentRepo, teacherRepo);
    }

    @Test
    void updateStudentRejectsNegativeFeeBalance() {
        StudentEntity student = new StudentEntity();
        student.setStudentid(7);
        when(studentRepo.findById(7)).thenReturn(Optional.of(student));
        StudentUpdate request = new StudentUpdate();
        request.setFeebalance(-0.01);

        assertThrows(IllegalArgumentException.class, () -> studentService.updateStudentByID(7, request));

        verify(studentRepo).findById(7);
        verifyNoInteractions(teacherRepo);
    }
}

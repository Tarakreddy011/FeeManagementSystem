package com.fms.demoFMS.FeeService;

import com.fms.demoFMS.DTOS.FeeRecpitDTO.RequestFeePaymentDTO;
import com.fms.demoFMS.DTOS.FeeRecpitDTO.ResponseFeePaymentDTO;
import com.fms.demoFMS.Entity.FeeHistory;
import com.fms.demoFMS.Entity.PrincipalEntity;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.Repos.FeeHistoryRepo;
import com.fms.demoFMS.Repos.StudentRepo;
import com.fms.demoFMS.Repos.TeacherRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeReciptTest {

    @Mock
    private StudentRepo studentRepo;

    @Mock
    private TeacherRepo teacherRepo;

    @Mock
    private FeeHistoryRepo feeHistoryRepo;

    @InjectMocks
    private FeeRecipt feeRecipt;

    private StudentEntity student;
    private PrincipalEntity assignedTeacher;

    @BeforeEach
    void setUp() {
        student = new StudentEntity();
        student.setStudentid(7);
        student.setFeebalance(100.00);

        assignedTeacher = new PrincipalEntity();
        assignedTeacher.setTeacherid(9);
        student.setTeacher(assignedTeacher);
    }

    @Test
    void roundsPaymentAndCreditsTheStudentsAssignedTeacher() {
        when(studentRepo.findById(7)).thenReturn(Optional.of(student));
        when(studentRepo.save(any(StudentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(feeHistoryRepo.save(any(FeeHistory.class))).thenAnswer(invocation -> {
            FeeHistory history = invocation.getArgument(0);
            history.setFeepaymentId(1);
            return history;
        });

        RequestFeePaymentDTO request = new RequestFeePaymentDTO();
        request.setStudentId(7);
        request.setTeacherId(8);
        request.setAmount(0.105);

        ResponseFeePaymentDTO response = feeRecipt.feereciptgeneration(request);

        ArgumentCaptor<FeeHistory> savedHistory = ArgumentCaptor.forClass(FeeHistory.class);
        verify(feeHistoryRepo).save(savedHistory.capture());
        assertEquals(0.11, response.getAmount());
        assertEquals(99.89, response.getNewfeebalanceafterpayment());
        assertEquals(9, response.getTeacherId());
        assertEquals(0.11, savedHistory.getValue().getAmount());
        assertEquals(assignedTeacher, savedHistory.getValue().getTeacher());
        verifyNoInteractions(teacherRepo);
    }

    @Test
    void rejectsNonPositiveAndNonFinitePaymentsWithoutDatabaseAccess() {
        for (double amount : new double[]{0, -0.01, Double.NaN, Double.POSITIVE_INFINITY}) {
            RequestFeePaymentDTO request = new RequestFeePaymentDTO();
            request.setStudentId(7);
            request.setAmount(amount);

            assertThrows(IllegalArgumentException.class, () -> feeRecipt.feereciptgeneration(request));
        }

        verifyNoInteractions(studentRepo, teacherRepo, feeHistoryRepo);
    }

    @Test
    void rejectsPaymentsThatRoundToZeroWithoutWriting() {
        when(studentRepo.findById(7)).thenReturn(Optional.of(student));

        RequestFeePaymentDTO request = new RequestFeePaymentDTO();
        request.setStudentId(7);
        request.setAmount(0.004);

        assertThrows(IllegalArgumentException.class, () -> feeRecipt.feereciptgeneration(request));

        verify(studentRepo).findById(7);
        verifyNoMoreInteractions(studentRepo);
        verifyNoInteractions(teacherRepo, feeHistoryRepo);
    }

    @Test
    void rejectsRoundedOverpaymentWithoutWriting() {
        student.setFeebalance(0.10);
        when(studentRepo.findById(7)).thenReturn(Optional.of(student));

        RequestFeePaymentDTO request = new RequestFeePaymentDTO();
        request.setStudentId(7);
        request.setAmount(0.105);

        assertThrows(IllegalArgumentException.class, () -> feeRecipt.feereciptgeneration(request));

        verify(studentRepo).findById(7);
        verifyNoMoreInteractions(studentRepo);
        verifyNoInteractions(teacherRepo, feeHistoryRepo);
    }
}

package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.FeeRecpitDTO.RequestFeePaymentDTO;
import com.fms.demoFMS.DTOS.FeeRecpitDTO.ResponseFeePaymentDTO;
import com.fms.demoFMS.Entity.StudentEntity;
import com.fms.demoFMS.FeeService.FeeRecipt;
import com.fms.demoFMS.Repos.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/paymyfee/student")
public class FeePaymentController {

    private final FeeRecipt feeRecipt;
    private final StudentRepo studentRepo;

    @Autowired
    public FeePaymentController(FeeRecipt feeRecipt, StudentRepo studentRepo) {
        this.feeRecipt = feeRecipt;
        this.studentRepo = studentRepo;
    }

    @PostMapping("/student/{id}/feepayment")
    public ResponseEntity<ResponseFeePaymentDTO> addfeetostuent(
            @PathVariable(name = "id", required = false) Integer id,
            @RequestBody RequestFeePaymentDTO request) {
        if ((request.getStudentId() == null || request.getStudentId() == 0) && id != null) {
            request.setStudentId(id);
        }
        ResponseFeePaymentDTO response = feeRecipt.feereciptgeneration(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pay")
    public ResponseEntity<ResponseFeePaymentDTO> payFee(
            @RequestBody RequestFeePaymentDTO request,
            Authentication authentication) {
        if (request.getStudentId() == null || request.getStudentId() == 0) {
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                Optional<StudentEntity> studentOpt = studentRepo.findByEmailIgnoreCase(username)
                        .or(() -> studentRepo.findByStudentnameIgnoreCase(username))
                        .or(() -> studentRepo.findByEmail(username))
                        .or(() -> studentRepo.findByStudentname(username));
                studentOpt.ifPresent(s -> request.setStudentId(s.getStudentid()));
            }
        }
        ResponseFeePaymentDTO response = feeRecipt.feereciptgeneration(request);
        return ResponseEntity.ok(response);
    }
}


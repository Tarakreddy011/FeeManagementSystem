package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.FeeRecpitDTO.RequestFeePaymentDTO;
import com.fms.demoFMS.DTOS.FeeRecpitDTO.ResponseFeePaymentDTO;
import com.fms.demoFMS.FeeService.FeeRecipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paymyfee/student")
public class FeePaymentController {

    private final FeeRecipt feeRecipt;

    @Autowired
    public FeePaymentController(FeeRecipt feeRecipt) {
        this.feeRecipt = feeRecipt;
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
}

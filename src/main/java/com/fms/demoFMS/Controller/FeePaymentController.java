package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.FeeRecpitDTO.RequestFeePaymentDTO;
import com.fms.demoFMS.DTOS.FeeRecpitDTO.ResponseFeePaymentDTO;
import com.fms.demoFMS.FeeService.FeeRecipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paymyfee/student")
public class FeePaymentController {

    @Autowired
    FeeRecipt feeRecipt;

    @PostMapping("/student/{id}/feepayment")
    public ResponseEntity<ResponseFeePaymentDTO> addfeetostuent(@RequestBody RequestFeePaymentDTO request){
        ResponseFeePaymentDTO response = feeRecipt.feereciptgeneration(request);
        return ResponseEntity.ok(response);
    }
}

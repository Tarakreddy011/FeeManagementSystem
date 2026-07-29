package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.AddFeeUpdateRequestDTO;
import com.fms.demoFMS.DTOS.AddFeeUpdateResponseDTO;
import com.fms.demoFMS.Service.FeeUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeeUpdateController {

    @Autowired
    FeeUpdateService feeUpdateService;

    @PostMapping("student/{id}/fee")
    public ResponseEntity<AddFeeUpdateResponseDTO> addfeetostuent(@RequestBody AddFeeUpdateRequestDTO request){
        AddFeeUpdateResponseDTO response = feeUpdateService.setfeeToStudentById(request);
        return ResponseEntity.ok(response);
    }
}

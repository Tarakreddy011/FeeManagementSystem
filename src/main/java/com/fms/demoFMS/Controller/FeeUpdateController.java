package com.fms.demoFMS.Controller;

import com.fms.demoFMS.DTOS.AddFee.AddFeeUpdateRequestDTO;
import com.fms.demoFMS.DTOS.AddFee.AddFeeUpdateResponseDTO;
import com.fms.demoFMS.Service.FeeUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacherupdate")
public class FeeUpdateController {

    private final FeeUpdateService feeUpdateService;

    @Autowired
    public FeeUpdateController(FeeUpdateService feeUpdateService) {
        this.feeUpdateService = feeUpdateService;
    }

    @PostMapping("/student/{id}/fee")
    public ResponseEntity<AddFeeUpdateResponseDTO> addfeetostuent(
            @PathVariable(name = "id", required = false) Integer id,
            @RequestBody AddFeeUpdateRequestDTO request) {
        if ((request.getStudentid() == null || request.getStudentid() == 0) && id != null) {
            request.setStudentid(id);
        }
        AddFeeUpdateResponseDTO response = feeUpdateService.setfeeToStudentById(request);
        return ResponseEntity.ok(response);
    }
}

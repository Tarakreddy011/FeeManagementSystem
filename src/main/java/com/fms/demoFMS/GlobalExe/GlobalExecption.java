package com.fms.demoFMS.GlobalExe;


import com.fms.demoFMS.DTOS.Execptions.ExecptionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExecption {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExecptionDTO> handleRuntimeException(RuntimeException e, HttpServletRequest httpServletRequest) {
        ExecptionDTO execptionDTO = new ExecptionDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                httpServletRequest.getRequestURI()
        );
        return ResponseEntity.status(500).
                body(execptionDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(500).
                body(e.getMessage());
    }
}

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExecptionDTO> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        ExecptionDTO execptionDTO = new ExecptionDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(execptionDTO);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExecptionDTO> handleRuntimeException(RuntimeException e, HttpServletRequest httpServletRequest) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = e.getMessage() != null ? e.getMessage() : "";

        if (msg.toLowerCase().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        }

        ExecptionDTO execptionDTO = new ExecptionDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                e.getMessage(),
                httpServletRequest.getRequestURI()
        );
        return ResponseEntity.status(status).body(execptionDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExecptionDTO> handleException(Exception e, HttpServletRequest request) {
        ExecptionDTO execptionDTO = new ExecptionDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(execptionDTO);
    }
}

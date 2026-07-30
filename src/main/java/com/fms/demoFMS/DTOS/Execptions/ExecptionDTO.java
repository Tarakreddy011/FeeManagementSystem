package com.fms.demoFMS.DTOS.Execptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ExecptionDTO {

    private LocalDateTime timestamp;
    private String message;
    private int statuscode;
    private String error;
    private String path;

    // Custom constructor for convenience
    public ExecptionDTO(LocalDateTime timestamp, int statuscode, String error, String message, String path) {
        this.timestamp = timestamp;
        this.statuscode = statuscode;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}

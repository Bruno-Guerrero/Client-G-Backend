package org.guerrero.bruno.clientg.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private int status;
    private String exception;
    private String message;
    private String requestDescription;
    private LocalDateTime timestamp;
}

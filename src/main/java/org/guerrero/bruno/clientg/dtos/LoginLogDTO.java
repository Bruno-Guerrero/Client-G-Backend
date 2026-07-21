package org.guerrero.bruno.clientg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginLogDTO {
    private Long id;
    private LocalDateTime dateTime;
    private String username;
}

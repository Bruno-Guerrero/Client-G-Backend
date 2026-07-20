package org.guerrero.bruno.clientg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDeleteDTO {
    private Long id;
    private Boolean isActive;
}

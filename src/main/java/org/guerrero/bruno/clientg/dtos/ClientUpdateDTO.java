package org.guerrero.bruno.clientg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientUpdateDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String ruc;
    private String razonSocial;
    private Integer automaticPaymentDay;
    private Boolean automaticPaymentAvailable;
}

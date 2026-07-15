package org.guerrero.bruno.clientg.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    private String jwtToken;
    private Long id;
    private String authorities;
}

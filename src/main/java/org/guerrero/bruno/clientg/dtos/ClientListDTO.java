package org.guerrero.bruno.clientg.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.Payment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientListDTO {
    private Long id;
    private String ruc;
    private String razonSocial;
    private String completeName;
    private Long numberPendingPayments;
    private Long numberLatePayments;
    private Integer automaticPaymentDay;
    private Boolean automaticPaymentAvailable;
    private LocalDateTime createdAt;


}

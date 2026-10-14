package org.guerrero.bruno.clientg.dtos;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.guerrero.bruno.clientg.entities.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

//Body de PUT /payments/{paymentId}: reemplaza todos los campos editables del pago (el cliente no cambia)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentUpdateDTO {
    @Enumerated(EnumType.STRING)
    private Payment.PaymentMethod paymentMethod; //TRANSFERENCIA o EFECTIVO
    @Enumerated(EnumType.STRING)
    private Payment.Currency currency;
    @Enumerated(EnumType.STRING)
    private Payment.Status status;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentReason;
}

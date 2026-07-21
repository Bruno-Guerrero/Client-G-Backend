package org.guerrero.bruno.clientg.dtos;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.guerrero.bruno.clientg.entities.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentListDTO {
    private Long id;

    @Enumerated(EnumType.STRING)
    private Payment.PaymentMethod paymentMethod; //TRANSFERENCIA o EFECTIVO

    @Enumerated(EnumType.STRING)
    private Payment.Currency currency;

    @Enumerated(EnumType.STRING)
    private Payment.Status status;

    private BigDecimal amount;

    //Fecha en la que se efectuó el pago, la ingresa el Accountant, ya que no necesariamente se pagó el mismo dia en el que se registró
    private LocalDate paymentDate;
    private String paymentReason;




    //Fecha y hora en la que se generó el registro de payment (se genera automaticamente el 30 de cada mes)
    private LocalDateTime createdAt;





}

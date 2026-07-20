package org.guerrero.bruno.clientg.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; //TRANSFERENCIA o EFECTIVO

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal amount;

    //Fecha en la que se efectuó el pago, la ingresa el Accountant, ya que no necesariamente se pagó el mismo dia en el que se registró
    private LocalDate paymentDate;
    private String paymentReason;



    //Fecha y hora en la que se generó el registro de payment (se genera automaticamente el 30 de cada mes)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;


//ENUMS
    public enum PaymentMethod{
        TRANSFERENCIA,
        EFECTIVO
    }
    public enum Currency{
        PEN,
        US,
        EU
    }
    public enum Status{
        PENDIENTE,
        PAGADO,
        RETRASO
    }
}

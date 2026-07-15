package org.guerrero.bruno.clientg.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; //TRANSFERENCIA o EFECTIVO

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentReason;

    private LocalDate createdAt;
    private LocalDate updatedAt;
    private LocalDate deletedAAt;

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
        PE,
        US,
        EU
    }
    public enum Status{
        PENDIENTE,
        PAGADO
    }
}

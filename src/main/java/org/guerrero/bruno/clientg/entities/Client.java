package org.guerrero.bruno.clientg.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    private String ruc;
    private String razonSocial;

    private Integer automaticPaymentDay;
    private Boolean automaticPaymentAvailable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "accountant_id")
    private Accountant accountant;

    @JsonIgnore
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY) //Para no jalar toda la lista cada que se llame a un objeto Client
    private List<Payment> payments;
}

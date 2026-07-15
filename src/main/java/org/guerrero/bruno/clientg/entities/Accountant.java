package org.guerrero.bruno.clientg.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accountants")
public class Accountant {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    private LocalDate createdAt;
    private LocalDate updatedAt;
    private LocalDate deletedAAt;

    private Boolean isActive;

    //Vinculo one to one con la entidad User
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    //Vinculo one to many con la entidad client
    @JsonIgnore
    @OneToMany(mappedBy = "accountant", fetch = FetchType.EAGER)
    private List<Client> clients;
}

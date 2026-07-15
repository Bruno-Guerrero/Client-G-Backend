package org.guerrero.bruno.clientg.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    //->Authority
    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_authorities",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "authority_id", referencedColumnName = "id", nullable = false)
            }
    )
    private List<Authority> authorities;

    //el FetchType.EAGER esta siemore por defecto en relaciones ManyToOne o OneToOne
    @JsonIgnore
    @OneToOne (mappedBy = "user")
    private Accountant accountant;

    //el FetchType.LAZY esta siemore por defecto en relaciones OneToMany o ManyToMany
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<LoginLog> loginLogs;


}

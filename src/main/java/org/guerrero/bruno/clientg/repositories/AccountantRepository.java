package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.entities.Accountant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountantRepository extends JpaRepository<Accountant, Long> {
    Accountant findByUser_username(String username);
}

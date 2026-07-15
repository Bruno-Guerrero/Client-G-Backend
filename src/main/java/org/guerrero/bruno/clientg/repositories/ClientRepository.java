package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}

package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.dtos.ClientDeleteDTO;
import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.entities.Payment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findClientByAccountantAndIsActiveTrue(Accountant accountant);
    Client findClientById(Long clientId);

    List<Client> findAllByAutomaticPaymentDayGreaterThanEqualAndAutomaticPaymentAvailableTrueAndIsActiveTrue(Integer automaticPaymentDay);

    List<Client> findAllByAutomaticPaymentDayAndAutomaticPaymentAvailableTrueAndIsActiveTrue(Integer automaticPaymentDay);

}

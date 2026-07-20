package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    //No deberia pertenecer a este repository, lo dejaré aqui por esta ocasión
    Long countByClientAndStatus(Client client, Payment.Status status);

    List<Payment> findAllByClient_Id(Long clientId);


}

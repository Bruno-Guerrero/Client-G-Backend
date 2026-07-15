package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

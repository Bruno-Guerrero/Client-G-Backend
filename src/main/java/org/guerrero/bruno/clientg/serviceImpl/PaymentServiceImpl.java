package org.guerrero.bruno.clientg.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.guerrero.bruno.clientg.dtos.PaymentDTO;
import org.guerrero.bruno.clientg.dtos.PaymentListDTO;
import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.entities.Payment;
import org.guerrero.bruno.clientg.repositories.AccountantRepository;
import org.guerrero.bruno.clientg.repositories.ClientRepository;
import org.guerrero.bruno.clientg.repositories.PaymentRepository;
import org.guerrero.bruno.clientg.services.PaymentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    @Override
    public PaymentDTO registerPayment(PaymentDTO paymentDTO) {

        //String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        Client client = clientRepository.findClientById(paymentDTO.getClientId());

        Payment payment = new Payment(
                null,
                paymentDTO.getPaymentMethod(),
                paymentDTO.getCurrency(),
                paymentDTO.getStatus(),
                paymentDTO.getAmount(),
                paymentDTO.getPaymentDate(),
                paymentDTO.getPaymentReason(),
                LocalDateTime.now(),
                null,
                null,
                true,
                client
        );

        paymentRepository.save(payment);

        return paymentDTO;
    }

    @Override
    public List<PaymentListDTO> getPaymentsDTO(Long clientId) {

        List<Payment> payments = paymentRepository.findAllByClient_Id(clientId);

        List<PaymentListDTO> paymentsDTO = payments.stream()
                .filter(p-> p.getIsActive())
                .map(
                        p -> new PaymentListDTO(
                                p.getId(),
                                p.getPaymentMethod(),
                                p.getCurrency(),
                                p.getStatus(),
                                p.getAmount(),
                                p.getPaymentDate(),
                                p.getPaymentReason(),
                                p.getCreatedAt()
                )).toList();

        return paymentsDTO;
    }

    @Override
    public void generateAutomaticPayment(Client client) {
        Payment payment = new Payment(
                null,
                null,
                null,
                Payment.Status.PENDIENTE,
                null,
                null,
                null,
                LocalDateTime.now(),
                null,
                null,
                true,
                client
        );
        paymentRepository.save(payment);
    }
}

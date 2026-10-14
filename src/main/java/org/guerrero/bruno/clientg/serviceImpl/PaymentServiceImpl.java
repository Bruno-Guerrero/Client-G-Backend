package org.guerrero.bruno.clientg.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.guerrero.bruno.clientg.dtos.PaymentDTO;
import org.guerrero.bruno.clientg.dtos.PaymentListDTO;
import org.guerrero.bruno.clientg.dtos.PaymentUpdateDTO;
import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.entities.Payment;
import org.guerrero.bruno.clientg.exceptions.InvalidOperationException;
import org.guerrero.bruno.clientg.exceptions.ResourceNotFoundException;
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
                .map(this::toListDTO)
                .toList();

        return paymentsDTO;
    }

    @Override
    public PaymentListDTO updatePayment(Long paymentId, PaymentUpdateDTO paymentUpdateDTO) {

        Payment payment = findPaymentOfLoggedAccountant(paymentId);

        //Regla de negocio: un pago anulado ya no se puede editar
        if (Boolean.FALSE.equals(payment.getIsActive())) {
            throw new InvalidOperationException("No se puede editar un pago anulado");
        }

        //Reemplazo completo de los campos editables (como PUT /clients/update); el cliente no cambia
        payment.setPaymentMethod(paymentUpdateDTO.getPaymentMethod());
        payment.setCurrency(paymentUpdateDTO.getCurrency());
        payment.setStatus(paymentUpdateDTO.getStatus());
        payment.setAmount(paymentUpdateDTO.getAmount());
        payment.setPaymentDate(paymentUpdateDTO.getPaymentDate());
        payment.setPaymentReason(paymentUpdateDTO.getPaymentReason());
        payment.setUpdatedAt(LocalDateTime.now());

        return toListDTO(paymentRepository.save(payment));
    }

    @Override
    public PaymentListDTO annulPayment(Long paymentId) {

        Payment payment = findPaymentOfLoggedAccountant(paymentId);

        if (Boolean.FALSE.equals(payment.getIsActive())) {
            throw new InvalidOperationException("El pago ya está anulado");
        }

        //Anulación lógica: el pago deja de listarse y de contarse, pero queda en la base de datos
        payment.setIsActive(false);
        payment.setDeletedAt(LocalDateTime.now());

        return toListDTO(paymentRepository.save(payment));
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

    //Busca el pago y verifica que pertenezca a un cliente del contador logueado.
    //Si es de otro contador se responde igual que si no existiera, para no revelar que existe.
    private Payment findPaymentOfLoggedAccountant(Long paymentId) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el pago " + paymentId));

        Client client = payment.getClient();
        Accountant accountant = client == null ? null : client.getAccountant();
        boolean belongsToLoggedAccountant = accountant != null
                && accountant.getUser() != null
                && userName.equals(accountant.getUser().getUsername());

        if (!belongsToLoggedAccountant) {
            throw new ResourceNotFoundException("No existe el pago " + paymentId);
        }
        return payment;
    }

    private PaymentListDTO toListDTO(Payment p) {
        return new PaymentListDTO(
                p.getId(),
                p.getPaymentMethod(),
                p.getCurrency(),
                p.getStatus(),
                p.getAmount(),
                p.getPaymentDate(),
                p.getPaymentReason(),
                p.getCreatedAt()
        );
    }
}

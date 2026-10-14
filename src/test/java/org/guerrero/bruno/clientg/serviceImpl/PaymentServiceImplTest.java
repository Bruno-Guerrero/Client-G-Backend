package org.guerrero.bruno.clientg.serviceImpl;

import org.guerrero.bruno.clientg.dtos.PaymentListDTO;
import org.guerrero.bruno.clientg.dtos.PaymentUpdateDTO;
import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.entities.Payment;
import org.guerrero.bruno.clientg.entities.User;
import org.guerrero.bruno.clientg.exceptions.InvalidOperationException;
import org.guerrero.bruno.clientg.exceptions.ResourceNotFoundException;
import org.guerrero.bruno.clientg.repositories.ClientRepository;
import org.guerrero.bruno.clientg.repositories.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//Pruebas unitarias (sin base de datos) de editar y anular pagos
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    private static final String LOGGED_ACCOUNTANT = "contador@ejemplo.com";

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ClientRepository clientRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void signIn() {
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(LOGGED_ACCOUNTANT, null, "ACCOUNTANT"));
    }

    @AfterEach
    void signOut() {
        SecurityContextHolder.clearContext();
    }

    //Pago automático pendiente (sin monto ni método) de un cliente del contador `owner`
    private Payment pendingPayment(boolean isActive, String owner) {
        User user = new User();
        user.setUsername(owner);
        Accountant accountant = new Accountant();
        accountant.setUser(user);
        Client client = new Client();
        client.setAccountant(accountant);
        return new Payment(15L, null, null, Payment.Status.PENDIENTE, null, null, null,
                LocalDateTime.of(2026, 9, 30, 0, 0), null, null, isActive, client);
    }

    private PaymentUpdateDTO paidUpdate() {
        return new PaymentUpdateDTO(Payment.PaymentMethod.EFECTIVO, Payment.Currency.PEN,
                Payment.Status.PAGADO, new BigDecimal("350.50"), LocalDate.of(2026, 10, 5),
                "Honorarios de setiembre");
    }

    @Test
    void updateRejectsAnAnnulledPayment() {
        when(paymentRepository.findById(15L)).thenReturn(Optional.of(pendingPayment(false, LOGGED_ACCOUNTANT)));

        InvalidOperationException error = assertThrows(InvalidOperationException.class,
                () -> paymentService.updatePayment(15L, paidUpdate()));

        assertEquals("No se puede editar un pago anulado", error.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void updateReplacesTheEditableFieldsOfAnActivePayment() {
        Payment payment = pendingPayment(true, LOGGED_ACCOUNTANT);
        when(paymentRepository.findById(15L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentListDTO updated = paymentService.updatePayment(15L, paidUpdate());

        assertEquals(Payment.Status.PAGADO, updated.getStatus());
        assertEquals(Payment.PaymentMethod.EFECTIVO, updated.getPaymentMethod());
        assertEquals(new BigDecimal("350.50"), updated.getAmount());
        assertEquals(LocalDate.of(2026, 10, 5), updated.getPaymentDate());
        assertEquals("Honorarios de setiembre", updated.getPaymentReason());
        //No cambia la fecha de registro, sí la de actualización
        assertEquals(LocalDateTime.of(2026, 9, 30, 0, 0), updated.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());
        assertTrue(payment.getIsActive());
    }

    @Test
    void annulMarksThePaymentAsInactive() {
        Payment payment = pendingPayment(true, LOGGED_ACCOUNTANT);
        when(paymentRepository.findById(15L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.annulPayment(15L);

        assertFalse(payment.getIsActive());
        assertNotNull(payment.getDeletedAt());
    }

    @Test
    void annulRejectsAnAlreadyAnnulledPayment() {
        when(paymentRepository.findById(15L)).thenReturn(Optional.of(pendingPayment(false, LOGGED_ACCOUNTANT)));

        assertThrows(InvalidOperationException.class, () -> paymentService.annulPayment(15L));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void anotherAccountantsPaymentLooksLikeItDoesNotExist() {
        when(paymentRepository.findById(15L)).thenReturn(Optional.of(pendingPayment(true, "otro@ejemplo.com")));

        assertThrows(ResourceNotFoundException.class, () -> paymentService.updatePayment(15L, paidUpdate()));
        assertThrows(ResourceNotFoundException.class, () -> paymentService.annulPayment(15L));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void unknownPaymentIsNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.annulPayment(99L));
    }
}

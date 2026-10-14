package org.guerrero.bruno.clientg.controllers;

import lombok.RequiredArgsConstructor;
import org.guerrero.bruno.clientg.dtos.PaymentDTO;
import org.guerrero.bruno.clientg.dtos.PaymentListDTO;
import org.guerrero.bruno.clientg.dtos.PaymentUpdateDTO;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.services.ClientService;
import org.guerrero.bruno.clientg.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/client-gest/payments") // http://localhost:8080/client-gest/payments
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/register") // http://localhost:8080/client-gest/payments/register
    public ResponseEntity<PaymentDTO> register(@RequestBody PaymentDTO paymentDTO){
        paymentService.registerPayment(paymentDTO);
        return new ResponseEntity<>(paymentDTO, HttpStatus.OK);
    }

    @GetMapping("{clientId}")
    public ResponseEntity<List<PaymentListDTO>> getAllPayments(@PathVariable Long clientId) {
        List<PaymentListDTO> paymentListDTO = paymentService.getPaymentsDTO(clientId);
        return new ResponseEntity<>(paymentListDTO, HttpStatus.OK);
    }

    //Edita un pago activo. Un pago anulado responde 406 (InvalidOperationException)
    @PutMapping("/{paymentId}") // http://localhost:8080/client-gest/payments/{paymentId}
    public ResponseEntity<PaymentListDTO> update(@PathVariable Long paymentId,
                                                 @RequestBody PaymentUpdateDTO paymentUpdateDTO){
        PaymentListDTO payment = paymentService.updatePayment(paymentId, paymentUpdateDTO);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    //Anulación lógica (isActive = false). No existe operación para revertirla
    @PutMapping("/{paymentId}/annul") // http://localhost:8080/client-gest/payments/{paymentId}/annul
    public ResponseEntity<PaymentListDTO> annul(@PathVariable Long paymentId){
        PaymentListDTO payment = paymentService.annulPayment(paymentId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }





}

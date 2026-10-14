package org.guerrero.bruno.clientg.services;

import org.guerrero.bruno.clientg.dtos.PaymentDTO;
import org.guerrero.bruno.clientg.dtos.PaymentListDTO;
import org.guerrero.bruno.clientg.dtos.PaymentUpdateDTO;
import org.guerrero.bruno.clientg.entities.Client;

import java.util.List;

public interface PaymentService {

    public PaymentDTO registerPayment(PaymentDTO paymentDTO);
    public List<PaymentListDTO> getPaymentsDTO(Long clientId);
    public PaymentListDTO updatePayment(Long paymentId, PaymentUpdateDTO paymentUpdateDTO);
    public PaymentListDTO annulPayment(Long paymentId);

    public void generateAutomaticPayment(Client client);



}

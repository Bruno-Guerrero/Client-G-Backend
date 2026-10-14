package org.guerrero.bruno.clientg.controllers;

import org.guerrero.bruno.clientg.dtos.*;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.services.ClientService;
import org.guerrero.bruno.clientg.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/client-gest/clients") // http://localhost:8080/client-gest/clients
public class ClientController {
    private final ClientService clientService;
    private final PaymentService paymentService;
    //la función de abajo no seria necesaria si implementamos la notacion @RequiredArgsContructor sobre la funcion del controller
    public ClientController(ClientService clientService, PaymentService paymentService) {

        this.clientService = clientService;
        this.paymentService = paymentService;
    }

    @PostMapping("/register")
    public ResponseEntity<ClientDTO> addClient(@RequestBody ClientDTO clientDTO){
        ClientDTO clientDto = clientService.addDTO(clientDTO);

        return new ResponseEntity<>(clientDto, HttpStatus.CREATED);
    }
    @GetMapping("")
    public ResponseEntity<List<ClientListDTO>> listAccountantClients(){

       List<ClientListDTO> clientList = clientService.getClientsListDTO();

       return new ResponseEntity<>(clientList, HttpStatus.OK);
    }
    @PutMapping("/is-active")//La arquitectura REST dicta eque en la url se mande el id del objeto que se modificará, enviarlo en un dto es mas similar a la arquitectura RCP
    public ResponseEntity<String> updateIsActive(@RequestBody ClientDeleteDTO client){
        String confirmationMesaage = clientService.deleteOrRecoverClientById(client.getId(),client.getIsActive());
        return new ResponseEntity<>(confirmationMesaage, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ClientUpdateDTO> updateClient(@RequestBody ClientUpdateDTO clientUpdateDTO){
        ClientUpdateDTO client = clientService.updateDTO(clientUpdateDTO);
        return new ResponseEntity<>(client, HttpStatus.OK);
    }
/*
    @GetMapping("/{id}/payments")
    public ResponseEntity<List<PaymentListDTO>> getPayments(@PathVariable Long id){
        List<PaymentListDTO> paymentsDTO = paymentService.getPaymentsDTO(id);
        return new ResponseEntity<>(paymentsDTO, HttpStatus.OK);
    }
*/
    @GetMapping("/{clientId}")//para el admin
    public ResponseEntity<ClientListDTO> getClientById(@PathVariable Long clientId){
        ClientListDTO clientDTO = clientService.getClientListDTOById(clientId);
        return new  ResponseEntity<>(clientDTO, HttpStatus.OK);
    }


    /* No tiene utilidad
    @PostMapping("/{clientId}/automatic-payment")
    public ResponseEntity<Void> generateManualInvoice(@PathVariable Long clientId) {

        Client client = clientService.findById(clientId);
        paymentService.generateAutomaticPayment(client);


        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    */
}

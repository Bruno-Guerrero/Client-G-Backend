package org.guerrero.bruno.clientg.controllers;

import org.guerrero.bruno.clientg.services.ClientService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/clientGest") // http://localhost:8080/clientGest
public class ClientController {
    private final ClientService clientService;
    //la función de abajo no seria necesaria si implementamos la notacion @RequiredArgsContructor sobre la funcion del controller
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
}

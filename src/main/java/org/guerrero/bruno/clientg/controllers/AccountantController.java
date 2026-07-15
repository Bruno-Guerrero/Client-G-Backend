package org.guerrero.bruno.clientg.controllers;

import org.guerrero.bruno.clientg.services.AccountantService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/clientGest") // http://localhost:8080/clientGest
public class AccountantController {
    private final AccountantService accountantService;
    //la función de abajo no seria necesaria si implementamos la notacion @RequiredArgsContructor sobre la funcion del controller
    public AccountantController(AccountantService accountantService) {
        this.accountantService = accountantService;
    }
}

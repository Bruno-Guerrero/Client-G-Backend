package org.guerrero.bruno.clientg.controllers;

import org.guerrero.bruno.clientg.services.LoginLogService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/client-gest") // http://localhost:8080/clientGest
public class LoginLogController {
    /*@Autowired
    PaymentService pymentService:*/ // es la version no recomendada, el autowired ademas ahora lo generamos de forma automatica eal detectar que es el unicoo service

    private final LoginLogService loginLogService;
    //la función de abajo no seria necesaria si implementamos la notacion @RequiredArgsContructor sobre la funcion del controller
    public LoginLogController(LoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }


}

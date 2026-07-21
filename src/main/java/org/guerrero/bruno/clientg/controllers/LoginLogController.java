package org.guerrero.bruno.clientg.controllers;

import org.guerrero.bruno.clientg.dtos.LoginLogDTO;
import org.guerrero.bruno.clientg.services.LoginLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/client-gest/logins") // http://localhost:8080/clientGest
public class LoginLogController {
    /*@Autowired
    PaymentService pymentService:*/ // es la version no recomendada, el autowired ademas ahora lo generamos de forma automatica eal detectar que es el unicoo service

    private final LoginLogService loginLogService;
    //la función de abajo no seria necesaria si implementamos la notacion @RequiredArgsContructor sobre la funcion del controller
    public LoginLogController(LoginLogService loginLogService) {
        this.loginLogService = loginLogService;
    }


    @GetMapping("/{user-id}")
    public ResponseEntity<List<LoginLogDTO>> listAllLogins(@PathVariable Long userId){
        List<LoginLogDTO> loginList = loginLogService.listLoginLogsByAccountant(userId);
        return new ResponseEntity<>(loginList, HttpStatus.OK);
    }


}

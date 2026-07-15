package org.guerrero.bruno.clientg.controllers;

import org.guerrero.bruno.clientg.dtos.AccountantDTO;
import org.guerrero.bruno.clientg.services.AccountantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/client-gest") // http://localhost:8080/clientGest
public class AccountantController {
    private final AccountantService accountantService;
    //la función de abajo no seria necesaria si implementamos la notacion @RequiredArgsContructor sobre la funcion del controller
    public AccountantController(AccountantService accountantService) {
        this.accountantService = accountantService;
    }

    @PostMapping("/accountants")
    public ResponseEntity<AccountantDTO> registerAccountant(@RequestBody AccountantDTO accountantDTO) {
        AccountantDTO accountantDto = accountantService.addDTO(accountantDTO);
        return new ResponseEntity<>(accountantDto, HttpStatus.CREATED);
    }

}

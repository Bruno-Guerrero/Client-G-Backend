package org.guerrero.bruno.clientg;

import org.guerrero.bruno.clientg.entities.Authority;
import org.guerrero.bruno.clientg.services.AuthorityService;
import org.guerrero.bruno.clientg.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientGApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientGApplication.class, args);
    }

    @Bean
    public CommandLineRunner startConfiguration(UserService userService, AuthorityService authorityService){
        return args ->{

            Authority authority1 = authorityService.add(new Authority(null, "ADMIN", null));
            Authority authority2 = authorityService.add(new Authority(null, "ACCOUNTANT", null));





        };




    }

}

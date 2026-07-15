package org.guerrero.bruno.clientg;

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
    public CommandLineRunner startConfiguration(UserService userService){
        return args ->{







        };




    }

}

package org.guerrero.bruno.clientg.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.guerrero.bruno.clientg.dtos.AccountantDTO;
import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.Authority;
import org.guerrero.bruno.clientg.entities.User;
import org.guerrero.bruno.clientg.exceptions.KeyRepeatedDataExeception;
import org.guerrero.bruno.clientg.repositories.AccountantRepository;
import org.guerrero.bruno.clientg.repositories.UserRepository;
import org.guerrero.bruno.clientg.services.AccountantService;
import org.guerrero.bruno.clientg.services.AuthorityService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountantServiceImpl implements AccountantService {


    private final AccountantRepository accountantRepository;
    private final UserRepository userRepository;
    private final AuthorityService authorityService;

    private List<Authority> authoritiesFromString(String authorities) {

        List<Authority> authorityList = new ArrayList<>();

        List<String> authorityStringList = Arrays.stream(authorities.split(";")).toList();

        for (String authorityString : authorityStringList) {
            Authority authority = authorityService.findByName(authorityString);
            if (authority != null) {
                authorityList.add(authority);
            }
        }
        return authorityList;
    }

    @Override
    public AccountantDTO addDTO(AccountantDTO accountantDTO) {
        //La autoridad utilizando este metodo se asigna automaticamente, a diferencia que is se hiciera con el userController, cuya autoridad deberá ser dada desde el mismo UserDTO
        List<Authority> authorityList = authoritiesFromString("ACCOUNTANT");

        List<User> users = userRepository.findAll();
        for (User u: users){
            if (accountantDTO.getUsername().equals(u.getUsername())){
                throw new KeyRepeatedDataExeception("El nombre de usuario: " + u.getUsername() +" ya ha está en uso");
            }
        }


        User newUser = new User(null, accountantDTO.getUsername(),
                new BCryptPasswordEncoder().encode(accountantDTO.getPassword()), authorityList, null, null);

        userRepository.save(newUser);



        Accountant newAccountant = new Accountant(
                null, accountantDTO.getFirstName(), accountantDTO.getLastName(),
                LocalDateTime.now(), null, null, true, newUser, null
        );

        accountantRepository.save(newAccountant);


        return accountantDTO;
    }

    @Override
    public AccountantDTO updateDTO(AccountantDTO accountantDto) {
        return null;
    }

    @Override
    public AccountantDTO deleteDTO(AccountantDTO accountantDto) {
        return null;
    }

    @Override
    public List<AccountantDTO> findAll() {
        return List.of();
    }
}

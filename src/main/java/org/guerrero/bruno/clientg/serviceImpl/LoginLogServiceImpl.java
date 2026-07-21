package org.guerrero.bruno.clientg.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.guerrero.bruno.clientg.dtos.LoginLogDTO;
import org.guerrero.bruno.clientg.entities.LoginLog;
import org.guerrero.bruno.clientg.entities.User;
import org.guerrero.bruno.clientg.repositories.LoginLogRepository;
import org.guerrero.bruno.clientg.repositories.UserRepository;
import org.guerrero.bruno.clientg.services.LoginLogService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogRepository loginLogRepository;
    private final UserRepository userRepository;
    @Override
    public List<LoginLogDTO> listLoginLogsByAccountant(Long userId) {

        List<LoginLog> logins = loginLogRepository.findAllByUser_Id(userId);

        String username = userRepository.findById(userId).get().getUsername();

        List<LoginLogDTO> loginsDTO = logins.stream()
                .map(
                        l -> new LoginLogDTO(
                                null,
                                LocalDateTime.now(),
                                username

                )).toList();

        return loginsDTO;
    }

    @Override
    public LoginLog addLoginLog(User user) {

        LoginLog login = new LoginLog(
                null,
                LocalDateTime.now(),
                user
        );
        loginLogRepository.save(login);

        return login;
    }

}

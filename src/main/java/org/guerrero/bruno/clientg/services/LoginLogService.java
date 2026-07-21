package org.guerrero.bruno.clientg.services;

import org.guerrero.bruno.clientg.dtos.LoginLogDTO;
import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.LoginLog;
import org.guerrero.bruno.clientg.entities.User;

import java.util.List;

public interface LoginLogService {
    public List<LoginLogDTO> listLoginLogsByAccountant(Long userId);

    public LoginLog addLoginLog(User user);
}

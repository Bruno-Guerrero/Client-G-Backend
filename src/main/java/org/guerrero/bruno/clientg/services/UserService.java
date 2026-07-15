package org.guerrero.bruno.clientg.services;

import org.guerrero.bruno.clientg.dtos.UserDTO;
import org.guerrero.bruno.clientg.entities.User;

public interface UserService {
    public User add(User user);
    public User findById(Long id);
    public User findByUsername(String username);
    public UserDTO addDTO(UserDTO userDTO);
}

package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);

}

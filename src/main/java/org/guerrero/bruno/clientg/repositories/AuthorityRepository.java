package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    public Authority findByName(String name);

}


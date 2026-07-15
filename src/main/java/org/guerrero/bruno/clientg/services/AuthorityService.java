package org.guerrero.bruno.clientg.services;


import org.guerrero.bruno.clientg.entities.Authority;

public interface AuthorityService {

    public Authority findById(Long id);
    public Authority findByName(String name);
    public Authority add(Authority authority);

}

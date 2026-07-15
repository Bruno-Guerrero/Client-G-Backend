package org.guerrero.bruno.clientg.serviceImpl;

import org.guerrero.bruno.clientg.entities.Authority;
import org.guerrero.bruno.clientg.repositories.AuthorityRepository;
import org.guerrero.bruno.clientg.services.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    AuthorityRepository authorityRepository;

    @Override
    public Authority findById(Long id) {
        return authorityRepository.findById(id).orElse(null);
    }

    @Override
    public Authority findByName(String name) {
        return authorityRepository.findByName(name);
    }

    @Override
    public Authority add(Authority authority) {
        return authorityRepository.save(authority);
    }
}


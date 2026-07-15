package org.guerrero.bruno.clientg.services;

import org.guerrero.bruno.clientg.dtos.AccountantDTO;

import java.util.List;

public interface AccountantService {
    public AccountantDTO findById(Long id);
    public AccountantDTO addDTO(AccountantDTO accountantDTO);
    public AccountantDTO updateDTO(AccountantDTO accountantDto);
    public AccountantDTO deleteDTO(AccountantDTO accountantDto);
    public List<AccountantDTO> findAll();

}

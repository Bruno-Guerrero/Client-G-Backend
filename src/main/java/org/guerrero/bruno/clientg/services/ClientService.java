package org.guerrero.bruno.clientg.services;

import org.guerrero.bruno.clientg.dtos.ClientDTO;
import org.guerrero.bruno.clientg.dtos.ClientDeleteDTO;
import org.guerrero.bruno.clientg.dtos.ClientListDTO;
import org.guerrero.bruno.clientg.dtos.ClientUpdateDTO;
import org.guerrero.bruno.clientg.entities.Client;

import java.util.List;

public interface ClientService {
    public ClientDTO addDTO(ClientDTO clientDTO);

    public List<ClientListDTO> getClientsListDTO();
    public String deleteOrRecoverClientById(Long clientId, Boolean isActive);
    public ClientUpdateDTO updateDTO(ClientUpdateDTO clientDTO);
    public Client findById(Long clientId);
    public ClientListDTO getClientListDTOById(Long clientId);
}

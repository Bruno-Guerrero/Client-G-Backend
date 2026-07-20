package org.guerrero.bruno.clientg.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.guerrero.bruno.clientg.dtos.ClientDTO;
import org.guerrero.bruno.clientg.dtos.ClientDeleteDTO;
import org.guerrero.bruno.clientg.dtos.ClientListDTO;
import org.guerrero.bruno.clientg.dtos.ClientUpdateDTO;
import org.guerrero.bruno.clientg.entities.Accountant;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.entities.Payment;
import org.guerrero.bruno.clientg.exceptions.InvalidDataRangeException;
import org.guerrero.bruno.clientg.repositories.AccountantRepository;
import org.guerrero.bruno.clientg.repositories.ClientRepository;
import org.guerrero.bruno.clientg.repositories.PaymentRepository;
import org.guerrero.bruno.clientg.services.ClientService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {


    private final ClientRepository clientRepository;
    private final AccountantRepository accountantRepository;
    private final PaymentRepository paymentRepository;
    @Override
    public ClientDTO addDTO(ClientDTO clientDTO) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        Accountant accountant = accountantRepository.findByUser_username(userName);

        List<Client> clientList = clientRepository.findClientByAccountantAndIsActiveTrue(accountant);

        if(clientList != null){
            for(Client client: clientList){
                if(client.getRuc().equals(clientDTO.getRuc())){
                    throw new RuntimeException("RUC ya registrado");
                }else if(client.getRazonSocial().equals(clientDTO.getRazonSocial())){
                    throw new RuntimeException("Razón social ya registrada");
                }
            }
        }

        LocalDateTime createdAt = LocalDateTime.now();

        Client client = new Client(
                null,
                clientDTO.getFirstName(),
                clientDTO.getLastName(),
                clientDTO.getRuc(),
                clientDTO.getRazonSocial(),
                clientDTO.getAutomaticPaymentDay(),
                clientDTO.getAutomaticPaymentAvailable(),
                createdAt,
                null,
                null,
                true,
                accountant,
                null
        );
        clientRepository.save(client);

        return clientDTO;

    }


    @Override
    public List<ClientListDTO> getClientsListDTO() {

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        Accountant accountant = accountantRepository.findByUser_username(userName);

        List<Client> accountantClientsList = clientRepository.findClientByAccountantAndIsActiveTrue(accountant);

        List<ClientListDTO> clientListDTO = accountantClientsList.stream()
                .filter(c -> c.getIsActive()!=null && c.getIsActive().equals(true))//Este filtrado sobra pero lo dejaré para tener constancia de que estuvo
                .map(
                        c -> new ClientListDTO(
                                c.getId(),
                                c.getRuc(),
                                c.getRazonSocial(),
                                c.getLastName() + ", " + c.getFirstName(),
                                paymentRepository.countByClientAndStatus(c, Payment.Status.PENDIENTE),
                                paymentRepository.countByClientAndStatus(c, Payment.Status.RETRASO),
                                c.getAutomaticPaymentDay(),
                                c.getAutomaticPaymentAvailable(),
                                c.getCreatedAt()

                        )).toList();
        if(clientListDTO.isEmpty()){
            throw new RuntimeException("No hay clientes registrados");
        }
        return clientListDTO;
    }

    @Override
    public String deleteOrRecoverClientById(Long clientId, Boolean isActive){

        Client client = clientRepository.findClientById(clientId);
        client.setIsActive(isActive);



        if (isActive){
            client.setDeletedAt(null);
            clientRepository.save(client);
            return "Cliente recuperado exitosamente";
        }else{
            client.setDeletedAt(LocalDateTime.now());
            clientRepository.save(client);
            return "Cliente eliminado exitosamente";
        }

    }

    @Override
    public ClientUpdateDTO updateDTO(ClientUpdateDTO clientDTO) {
        Client client = clientRepository.findClientById(clientDTO.getId());

        if (!clientDTO.getRuc().matches("\\d+")){
            throw new InvalidDataRangeException("Solo ingrese dígitos numéricos");
        }else if(clientDTO.getRuc().length()!=11){
            throw new InvalidDataRangeException("El RUC debe tener 11 digitos");
        }else if(!clientDTO.getRuc().startsWith("10") && !clientDTO.getRuc().startsWith("20") && !clientDTO.getRuc().startsWith("15")){
            //20 para empresa 10 para persona natural 15 para extranjeros
            throw new InvalidDataRangeException("El RUC debe iniciar con 10, 20 o 15");
        }

        client.setFirstName(clientDTO.getFirstName());
        client.setLastName(clientDTO.getLastName());
        client.setRuc(clientDTO.getRuc());
        client.setRazonSocial(clientDTO.getRazonSocial());

        client.setAutomaticPaymentDay(clientDTO.getAutomaticPaymentDay());
        client.setAutomaticPaymentAvailable(clientDTO.getAutomaticPaymentAvailable());

        return clientDTO;
    }

    @Override
    public Client findById(Long clientId){
        return clientRepository.findById(clientId).orElse(null);
    }


}

package com.uninter.baozi.service;

import com.uninter.baozi.dto.ClientRequest;
import com.uninter.baozi.model.ClientEntity;
import com.uninter.baozi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Long createClient(ClientRequest request) {

        if (request.name() == null || request.phoneNumber() == null) {
            throw new IllegalArgumentException("É necessário informar o nome e o telefone do cliente.");
        }

        ClientEntity clientEntity = new ClientEntity()
                .setName(request.name())
                .setPhoneNumber(request.phoneNumber());

        return clientRepository.save(clientEntity).getId();
    }

    public ClientEntity getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));
    }

    public List<ClientEntity> getAllClients() {
        return clientRepository.findAll();
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado com o ID: " + id);
        }
        clientRepository.deleteById(id);
    }
}

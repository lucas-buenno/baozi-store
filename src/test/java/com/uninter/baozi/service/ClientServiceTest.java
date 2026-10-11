package com.uninter.baozi.service;

import com.uninter.baozi.dto.ClientRequest;
import com.uninter.baozi.model.ClientEntity;
import com.uninter.baozi.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void createClient_shouldSaveAndReturnId() {
        ClientRequest request = new ClientRequest("Maria", "11999999999");
        when(clientRepository.save(any(ClientEntity.class))).thenAnswer(invocation -> {
            ClientEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        Long id = clientService.createClient(request);

        assertEquals(1L, id);

        ArgumentCaptor<ClientEntity> captor = ArgumentCaptor.forClass(ClientEntity.class);
        verify(clientRepository).save(captor.capture());
        assertEquals("Maria", captor.getValue().getName());
        assertEquals("11999999999", captor.getValue().getPhoneNumber());
    }

    @Test
    void createClient_shouldThrowWhenNameIsNull() {
        ClientRequest request = new ClientRequest(null, "11999999999");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.createClient(request)
        );

        assertEquals("É necessário informar o nome e o telefone do cliente.", exception.getMessage());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void createClient_shouldThrowWhenPhoneNumberIsNull() {
        ClientRequest request = new ClientRequest("Maria", null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.createClient(request)
        );

        assertEquals("É necessário informar o nome e o telefone do cliente.", exception.getMessage());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void getClientById_shouldReturnClient() {
        ClientEntity client = new ClientEntity().setId(1L).setName("Maria");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientEntity result = clientService.getClientById(1L);

        assertEquals(client, result);
    }

    @Test
    void getClientById_shouldThrowWhenNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.getClientById(99L)
        );

        assertEquals("Cliente não encontrado com o ID: 99", exception.getMessage());
    }

    @Test
    void getAllClients_shouldReturnList() {
        List<ClientEntity> clients = List.of(
                new ClientEntity().setId(1L).setName("Maria"),
                new ClientEntity().setId(2L).setName("João")
        );
        when(clientRepository.findAll()).thenReturn(clients);

        List<ClientEntity> result = clientService.getAllClients();

        assertEquals(clients, result);
        verify(clientRepository).findAll();
    }

    @Test
    void deleteClient_shouldDeleteWhenExists() {
        when(clientRepository.existsById(1L)).thenReturn(true);

        clientService.deleteClient(1L);

        verify(clientRepository).deleteById(1L);
    }

    @Test
    void deleteClient_shouldThrowWhenNotFound() {
        when(clientRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.deleteClient(99L)
        );

        assertEquals("Cliente não encontrado com o ID: 99", exception.getMessage());
        verify(clientRepository, never()).deleteById(any());
    }
}

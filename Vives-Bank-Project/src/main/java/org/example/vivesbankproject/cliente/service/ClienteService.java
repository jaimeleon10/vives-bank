package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.cliente.dto.ClienteRequest;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ClienteService {

    Page<ClienteResponse> getAll(Optional<String> dni, Optional<String> nombre, Optional<String> apellidos, Optional<String> email, Optional<String> telefono, Pageable pageable);

    ClienteResponse getById(UUID id);

    ClienteResponse save(ClienteRequest cliente);

    ClienteResponse update(UUID id, ClienteRequest cliente);

    void deleteById(UUID id);
}
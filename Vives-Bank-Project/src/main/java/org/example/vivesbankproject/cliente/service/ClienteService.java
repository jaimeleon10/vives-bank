package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.cliente.dto.ClienteRequest;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdateAdmin;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ClienteService {

    Page<Cliente> getAll(Optional<String> dni, Optional<String> nombre, Optional<String> apellidos, Optional<String> email, Optional<String> telefono, Pageable pageable);

    ClienteResponse getById(String id);

    ClienteResponse save(ClienteRequest cliente);

    ClienteResponse update(String id, ClienteRequestUpdate clienteRequestUpdate);

    ClienteResponse updateByAdmin(String id, ClienteRequestUpdateAdmin clienteRequestUpdateAdmin);

    void deleteById(String id);
}
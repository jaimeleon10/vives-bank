package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.cliente.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteService {

    Page<ClienteResponse> getAll(Optional<String> dni, Optional<String> nombre, Optional<String> apellidos, Optional<String> email, Optional<String> telefono, Pageable pageable);

    ClienteResponse getById(String id);

    ClienteResponse save(ClienteRequestSave cliente);

    ClienteResponse update(String id, ClienteRequestUpdate clienteRequestUpdate);

    ClienteResponse addCuentas(String id, ClienteCuentasRequest clienteCuentasRequest);

    ClienteResponse deleteCuentas(String id, ClienteCuentasRequest clienteCuentasRequest);

    void deleteById(String id);

    // ClienteResponseProductos getProductos(String id);
}
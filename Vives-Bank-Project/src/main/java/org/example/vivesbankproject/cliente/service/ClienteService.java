package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.cliente.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ClienteService {

    Page<ClienteResponse> getAll(Optional<String> dni, Optional<String> nombre, Optional<String> apellidos, Optional<String> email, Optional<String> telefono, Pageable pageable);

    ClienteResponse getById(String id);

    ClienteResponse getByDni(String dni);

    ClienteResponse save(ClienteRequestSave cliente);

    ClienteResponse update(String id, ClienteRequestUpdate clienteRequestUpdate);

    void deleteById(String id);

    ClienteResponse getUserAuthenticatedByGuid(String userGuid);

    ClienteResponse updateUserAuthenticated(String userGuid, ClienteRequestUpdate clienteRequestUpdate);

    String derechoAlOlvido(String userGuid);

    ClienteResponse updateDniFoto(String id, MultipartFile file);

    ClienteResponse updateProfileFoto(String id, MultipartFile file);

    ClienteProducto getCatalogue();
}
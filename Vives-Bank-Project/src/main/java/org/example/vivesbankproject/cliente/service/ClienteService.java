package org.example.vivesbankproject.cliente.service;

import org.example.vivesbankproject.cliente.dto.*;
import org.springframework.cache.annotation.CachePut;
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

    ClienteResponse getUserByGuid(String userGuid);

    @CachePut
    ClienteResponse updateDniFoto(String id, MultipartFile file);

    @CachePut
    ClienteResponse updateProfileFoto(String id, MultipartFile file);
}
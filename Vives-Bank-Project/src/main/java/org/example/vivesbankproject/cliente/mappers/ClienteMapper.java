package org.example.vivesbankproject.cliente.mappers;

import org.example.vivesbankproject.cliente.dto.ClienteRequest;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClienteMapper {


    public Cliente toCliente(ClienteRequest request) {
        return Cliente.builder()
                .dni(request.getDni())
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .fotoPerfil(request.getFotoPerfil())
                .fotoDni(request.getFotoDni())
                .build();
    }


    public Cliente toCliente(ClienteRequest request, UUID id) {
        return Cliente.builder()
                .id(id)
                .dni(request.getDni())
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .fotoPerfil(request.getFotoPerfil())
                .fotoDni(request.getFotoDni())
                .build();
    }


    public ClienteResponse toClienteResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .dni(cliente.getDni())
                .nombre(cliente.getNombre())
                .apellidos(cliente.getApellidos())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fotoPerfil(cliente.getFotoPerfil())
                .fotoDni(cliente.getFotoDni())
                .idMovimientos(cliente.getIdMovimientos())
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .build();
    }
}

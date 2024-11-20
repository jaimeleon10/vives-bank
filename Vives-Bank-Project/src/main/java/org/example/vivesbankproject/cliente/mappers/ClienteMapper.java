package org.example.vivesbankproject.cliente.mappers;

import org.example.vivesbankproject.cliente.dto.ClienteRequest;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClienteMapper {

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
                .cuentas(cliente.getCuentas())
                .user(cliente.getUser())
                .idMovimientos(cliente.getIdMovimientos())
                .build();
    }

    public Cliente toCliente(ClienteRequest request) {
        return Cliente.builder()
                .dni(request.getDni())
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .fotoPerfil(request.getFotoPerfil())
                .fotoDni(request.getFotoDni())
                .cuentas(request.getCuentas())
                .user(request.getUser())
                .build();
    }
}

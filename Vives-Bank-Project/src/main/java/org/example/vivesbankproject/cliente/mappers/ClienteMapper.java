package org.example.vivesbankproject.cliente.mappers;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdateAdmin;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClienteMapper {

    public ClienteResponse toClienteResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .guid(cliente.getGuid())
                .dni(cliente.getDni())
                .nombre(cliente.getNombre())
                .apellidos(cliente.getApellidos())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fotoPerfil(cliente.getFotoPerfil())
                .fotoDni(cliente.getFotoDni())
                .cuentas(cliente.getCuentas())
                .user(cliente.getUser())
                .idMovimientos(new ObjectId(cliente.getIdMovimientos()))
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .isDeleted(cliente.getIsDeleted())
                .build();
    }

    public Cliente toCliente(ClienteRequestSave clienteRequestSave) {
        return Cliente.builder()
                .dni(clienteRequestSave.getDni())
                .nombre(clienteRequestSave.getNombre())
                .apellidos(clienteRequestSave.getApellidos())
                .email(clienteRequestSave.getEmail())
                .telefono(clienteRequestSave.getTelefono())
                .fotoPerfil(clienteRequestSave.getFotoPerfil())
                .fotoDni(clienteRequestSave.getFotoDni())
                .cuentas(clienteRequestSave.getCuentas())
                .user(clienteRequestSave.getUser())
                .build();
    }

    public Cliente toClienteUpdate(ClienteRequestUpdate clienteRequestUpdate, Cliente cliente) {
        return Cliente.builder()
                .id(cliente.getId())
                .guid(cliente.getGuid())
                .dni(cliente.getDni())
                .nombre(clienteRequestUpdate.getNombre())
                .apellidos(clienteRequestUpdate.getApellidos())
                .email(clienteRequestUpdate.getEmail())
                .telefono(clienteRequestUpdate.getTelefono())
                .fotoPerfil(clienteRequestUpdate.getFotoPerfil())
                .fotoDni(clienteRequestUpdate.getFotoDni())
                .cuentas(cliente.getCuentas())
                .user(clienteRequestUpdate.getUser())
                .createdAt(cliente.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(clienteRequestUpdate.getIsDeleted())
                .build();
    }

    public Cliente toClienteUpdateAdmin(ClienteRequestUpdateAdmin clienteRequestUpdateAdmin, Cliente cliente) {
        return Cliente.builder()
                .id(cliente.getId())
                .guid(cliente.getGuid())
                .dni(cliente.getDni())
                .nombre(clienteRequestUpdateAdmin.getNombre())
                .apellidos(clienteRequestUpdateAdmin.getApellidos())
                .email(clienteRequestUpdateAdmin.getEmail())
                .telefono(clienteRequestUpdateAdmin.getTelefono())
                .fotoPerfil(clienteRequestUpdateAdmin.getFotoPerfil())
                .fotoDni(clienteRequestUpdateAdmin.getFotoDni())
                .cuentas(clienteRequestUpdateAdmin.getCuentas())
                .user(clienteRequestUpdateAdmin.getUser())
                .createdAt(cliente.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(clienteRequestUpdateAdmin.getIsDeleted())
                .build();
    }
}

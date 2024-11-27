package org.example.vivesbankproject.cliente.mappers;

import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.models.Direccion;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class ClienteMapper {

    public ClienteResponse toClienteResponse(Cliente cliente, String userId) {
        return ClienteResponse.builder()
                .guid(cliente.getGuid())
                .dni(cliente.getDni())
                .nombre(cliente.getNombre())
                .apellidos(cliente.getApellidos())
                .calle(cliente.getDireccion().getCalle())
                .numero(cliente.getDireccion().getNumero())
                .codigoPostal(cliente.getDireccion().getCodigoPostal())
                .piso(cliente.getDireccion().getPiso())
                .letra(cliente.getDireccion().getLetra())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fotoPerfil(cliente.getFotoPerfil())
                .fotoDni(cliente.getFotoDni())
                .userId(userId)
                .createdAt(cliente.getCreatedAt().toString())
                .updatedAt(cliente.getUpdatedAt().toString())
                .isDeleted(cliente.getIsDeleted())
                .build();
    }


    public Cliente toCliente(ClienteRequestSave clienteRequestSave, User user, Direccion direccion) {
        return Cliente.builder()
                .dni(clienteRequestSave.getDni())
                .nombre(clienteRequestSave.getNombre())
                .apellidos(clienteRequestSave.getApellidos())
                .direccion(direccion)
                .email(clienteRequestSave.getEmail())
                .telefono(clienteRequestSave.getTelefono())
                .fotoPerfil(clienteRequestSave.getFotoPerfil())
                .fotoDni(clienteRequestSave.getFotoDni())
                .user(user)
                .isDeleted(clienteRequestSave.getIsDeleted())
                .build();
    }

    public Cliente toClienteUpdate(ClienteRequestUpdate clienteRequestUpdate, Cliente cliente, User user, Direccion direccion) {
        return Cliente.builder()
                .id(cliente.getId())
                .guid(cliente.getGuid())
                .dni(cliente.getDni())
                .nombre(clienteRequestUpdate.getNombre())
                .apellidos(clienteRequestUpdate.getApellidos())
                .direccion(direccion)
                .email(clienteRequestUpdate.getEmail())
                .telefono(clienteRequestUpdate.getTelefono())
                .fotoPerfil(clienteRequestUpdate.getFotoPerfil())
                .fotoDni(clienteRequestUpdate.getFotoDni())
                .user(user)
                .createdAt(cliente.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(cliente.getIsDeleted())
                .build();
    }
}

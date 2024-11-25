package org.example.vivesbankproject.cliente.mappers;

import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaForClienteResponse;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class ClienteMapper {

    public ClienteResponse toClienteResponse(Cliente cliente, UserResponse user, Set<CuentaForClienteResponse> cuentas) {
        return ClienteResponse.builder()
                .guid(cliente.getGuid())
                .dni(cliente.getDni())
                .nombre(cliente.getNombre())
                .apellidos(cliente.getApellidos())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fotoPerfil(cliente.getFotoPerfil())
                .fotoDni(cliente.getFotoDni())
                .cuentas(cuentas)
                .user(user)
                .createdAt(cliente.getCreatedAt())
                .updatedAt(cliente.getUpdatedAt())
                .isDeleted(cliente.getIsDeleted())
                .build();
    }

    public ClienteForCuentaResponse toClienteDataResponse(Cliente cliente) {
        return ClienteForCuentaResponse.builder()
                .guid(cliente.getGuid())
                .dni(cliente.getDni())
                .nombre(cliente.getNombre())
                .apellidos(cliente.getApellidos())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fotoPerfil(cliente.getFotoPerfil())
                .fotoDni(cliente.getFotoDni())
                .build();
    }

    public Cliente toCliente(ClienteRequestSave clienteRequestSave, User user, Set<Cuenta> cuentas) {
        return Cliente.builder()
                .dni(clienteRequestSave.getDni())
                .nombre(clienteRequestSave.getNombre())
                .apellidos(clienteRequestSave.getApellidos())
                .email(clienteRequestSave.getEmail())
                .telefono(clienteRequestSave.getTelefono())
                .fotoPerfil(clienteRequestSave.getFotoPerfil())
                .fotoDni(clienteRequestSave.getFotoDni())
                .cuentas(cuentas)
                .user(user)
                .isDeleted(clienteRequestSave.getIsDeleted())
                .build();
    }

    public Cliente toClienteUpdate(ClienteRequestUpdate clienteRequestUpdate, Cliente cliente, User user) {
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
                .user(user)
                .createdAt(cliente.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(cliente.getIsDeleted())
                .build();
    }
}

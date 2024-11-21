package org.example.vivesbankproject.cliente.mappers;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.dto.ClienteRequest;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdateAdmin;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                .build();
    }

    public Cliente toCliente(ClienteRequest clienteRequest) {
        return Cliente.builder()
                .dni(clienteRequest.getDni())
                .nombre(clienteRequest.getNombre())
                .apellidos(clienteRequest.getApellidos())
                .email(clienteRequest.getEmail())
                .telefono(clienteRequest.getTelefono())
                .fotoPerfil(clienteRequest.getFotoPerfil())
                .fotoDni(clienteRequest.getFotoDni())
                .cuentas(clienteRequest.getCuentas())
                .user(clienteRequest.getUser())
                .build();
    }

    public Cliente toClienteUpdate(ClienteRequestUpdate clienteRequestUpdate, Cliente cliente) {
        return Cliente.builder()
                .dni(cliente.getDni())
                .nombre(clienteRequestUpdate.getNombre())
                .apellidos(clienteRequestUpdate.getApellidos())
                .email(clienteRequestUpdate.getEmail())
                .telefono(clienteRequestUpdate.getTelefono())
                .fotoPerfil(clienteRequestUpdate.getFotoPerfil())
                .fotoDni(clienteRequestUpdate.getFotoDni())
                .cuentas(cliente.getCuentas())
                .user(clienteRequestUpdate.getUser())
                .build();
    }

    public Cliente toClienteUpdateAdmin(ClienteRequestUpdateAdmin clienteRequestUpdateAdmin, Cliente cliente) {
        return Cliente.builder()
                .dni(cliente.getDni())
                .nombre(clienteRequestUpdateAdmin.getNombre())
                .apellidos(clienteRequestUpdateAdmin.getApellidos())
                .email(clienteRequestUpdateAdmin.getEmail())
                .telefono(clienteRequestUpdateAdmin.getTelefono())
                .fotoPerfil(clienteRequestUpdateAdmin.getFotoPerfil())
                .fotoDni(clienteRequestUpdateAdmin.getFotoDni())
                .cuentas(clienteRequestUpdateAdmin.getCuentas())
                .user(clienteRequestUpdateAdmin.getUser())
                .build();
    }
}

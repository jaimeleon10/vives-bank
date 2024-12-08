package org.example.vivesbankproject.rest.cliente.mappers;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.rest.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.models.Direccion;
import org.example.vivesbankproject.rest.users.models.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * Clase Mapper para convertir objetos entre las clases de solicitud, respuesta y entidades.
 * Se encarga de transformar objetos entre las capas de DTOs y entidades.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Component
public class ClienteMapper {

    /**
     * Convierte un objeto Cliente en un objeto ClienteResponse.
     *
     * @param cliente El objeto Cliente que se convertirá en ClienteResponse
     * @param userId El identificador del usuario
     * @return Una instancia de ClienteResponse con la información mapeada
     */
    @Schema(description = "Convierte un objeto Cliente en un ClienteResponse", implementation = ClienteResponse.class)
    public ClienteResponse toClienteResponse(
            @Schema(description = "El objeto Cliente que se convertirá en ClienteResponse", required = true) Cliente cliente,
            @Schema(description = "El identificador del usuario", example = "123") String userId) {

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

    /**
     * Convierte un objeto ClienteRequestSave en un objeto Cliente.
     *
     * @param clienteRequestSave El objeto de solicitud con datos de cliente
     * @param user El objeto User relacionado con el cliente
     * @param direccion La dirección asociada al cliente
     * @return Una instancia de Cliente con la información mapeada
     */
    @Schema(description = "Convierte un objeto ClienteRequestSave en un Cliente", implementation = Cliente.class)
    public Cliente toCliente(
            @Schema(description = "El objeto de solicitud con datos de cliente", required = true) ClienteRequestSave clienteRequestSave,
            @Schema(description = "El objeto User relacionado con el cliente", required = true) User user,
            @Schema(description = "La dirección asociada al cliente", required = true) Direccion direccion) {

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

    /**
     * Convierte un objeto ClienteRequestUpdate en una actualización de Cliente.
     *
     * @param clienteRequestUpdate El objeto de solicitud con información actualizada
     * @param cliente El objeto Cliente existente que se actualizará
     * @param user El objeto User relacionado con el cliente
     * @param direccion La nueva dirección asociada al cliente
     * @return Una instancia de Cliente con la información actualizada
     */
    @Schema(description = "Convierte un objeto ClienteRequestUpdate en Cliente para actualizar", implementation = Cliente.class)
    public Cliente toClienteUpdate(
            @Schema(description = "El objeto de solicitud con información actualizada", required = true) ClienteRequestUpdate clienteRequestUpdate,
            @Schema(description = "El objeto Cliente existente que se actualizará", required = true) Cliente cliente,
            @Schema(description = "El objeto User relacionado con el cliente", required = true) User user,
            @Schema(description = "La nueva dirección asociada al cliente", required = true) Direccion direccion) {

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
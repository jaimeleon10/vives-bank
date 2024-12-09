package org.example.vivesbankproject.rest.cliente.repositories;

import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de operaciones CRUD relacionadas con la entidad Cliente.
 * Proporciona métodos para realizar búsquedas avanzadas, validaciones y operaciones con la base de datos.
 * Utiliza Spring Data JPA para el acceso a datos.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    /**
     * Encuentra un cliente por su GUID único.
     *
     * @param guid El identificador global único del cliente.
     * @return Un objeto Optional con el cliente si se encuentra.
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Busca un cliente por su GUID",
            required = true
    )
    Optional<Cliente> findByGuid(String guid);

    /**
     * Encuentra un cliente por su DNI.
     *
     * @param dni El DNI del cliente.
     * @return Un objeto Optional con el cliente si se encuentra.
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Busca un cliente por su DNI",
            required = true
    )
    Optional<Cliente> findByDni(String dni);

    /**
     * Encuentra un cliente por su correo electrónico.
     *
     * @param email El correo electrónico del cliente.
     * @return Un objeto Optional con el cliente si se encuentra.
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Busca un cliente por su email",
            required = true
    )
    Optional<Cliente> findByEmail(String email);

    /**
     * Encuentra un cliente por su número de teléfono.
     *
     * @param telefono El número de teléfono del cliente.
     * @return Un objeto Optional con el cliente si se encuentra.
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Busca un cliente por su número de teléfono",
            required = true
    )
    Optional<Cliente> findByTelefono(String telefono);

    /**
     * Comprueba si ya existe un cliente con el GUID de usuario asociado.
     *
     * @param userGuid El GUID del usuario para comprobar la existencia en la base de datos.
     * @return `true` si existe, `false` en caso contrario.
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Comprueba si el cliente con el GUID de usuario existe",
            required = true
    )
    boolean existsByUserGuid(String userGuid);

    /**
     * Recupera un cliente por el GUID de usuario asignado.
     *
     * @param userGuid El GUID del usuario.
     * @return Un objeto Optional con el cliente si se encuentra.
     */
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Busca un cliente por el GUID de usuario",
            required = true
    )
    Optional<Cliente> findByUserGuid(String userGuid);
}
package org.example.vivesbankproject.rest.cuenta.repositories;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Repositorio para realizar operaciones de base de datos sobre la entidad Cuenta.
 * Extiende JpaRepository y JpaSpecificationExecutor para aprovechar las operaciones básicas
 * de acceso a datos y filtrado dinámico.
 *
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long>, JpaSpecificationExecutor<Cuenta> {

    /**
     * Busca una cuenta por su identificador global (GUID).
     *
     * @param guid Identificador global único de la cuenta
     * @return Opcional que contiene la cuenta si existe
     */
    @Operation(summary = "Buscar una cuenta por su identificador GUID", description = "Devuelve una cuenta si existe un GUID coincidente.")
    @Parameter(name = "guid", description = "Identificador global único de la cuenta a buscar", required = true)
    Optional<Cuenta> findByGuid(String guid);

    /**
     * Busca una cuenta por su IBAN.
     *
     * @param iban Número de cuenta IBAN para realizar la búsqueda
     * @return Opcional que contiene la cuenta si existe
     */
    @Operation(summary = "Buscar una cuenta por su IBAN", description = "Devuelve una cuenta si existe un IBAN coincidente.")
    @Parameter(name = "iban", description = "Código IBAN para buscar la cuenta", required = true)
    Optional<Cuenta> findByIban(String iban);

    /**
     * Busca todas las cuentas asociadas a un cliente por el identificador GUID de ese cliente.
     *
     * @param clienteGuid Identificador global del cliente
     * @return Lista de cuentas asociadas al cliente
     */
    @Operation(summary = "Buscar todas las cuentas de un cliente por su GUID", description = "Devuelve todas las cuentas relacionadas a un cliente específico por su GUID.")
    @Parameter(name = "clienteGuid", description = "Identificador global del cliente para la búsqueda", required = true)
    ArrayList<Cuenta> findAllByCliente_Guid(String clienteGuid);

    /**
     * Busca una cuenta por el identificador de su tarjeta.
     *
     * @param id Identificador de la tarjeta
     * @return Opcional que contiene la cuenta si existe
     */
    @Operation(summary = "Buscar una cuenta por el identificador de la tarjeta", description = "Devuelve una cuenta si existe una tarjeta coincidente con el identificador proporcionado.")
    @Parameter(name = "id", description = "Identificador de la tarjeta para la búsqueda", required = true)
    Optional<Cuenta> findByTarjetaId(Long id);
}
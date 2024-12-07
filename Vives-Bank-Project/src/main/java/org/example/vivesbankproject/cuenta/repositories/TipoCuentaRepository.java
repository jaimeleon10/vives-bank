package org.example.vivesbankproject.cuenta.repositories;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para realizar operaciones de base de datos sobre la entidad TipoCuenta.
 * Extiende JpaRepository y JpaSpecificationExecutor para realizar operaciones básicas
 * y filtrado dinámico sobre la base de datos.
 *
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Repository
public interface TipoCuentaRepository extends JpaRepository<TipoCuenta, Long>, JpaSpecificationExecutor<TipoCuenta> {

    /**
     * Busca un tipo de cuenta por su nombre.
     *
     * @param nombre Nombre del tipo de cuenta que se desea buscar
     * @return Opcional que contiene el tipo de cuenta si existe
     */
    @Operation(summary = "Buscar un tipo de cuenta por su nombre", description = "Devuelve un tipo de cuenta si existe un nombre coincidente.")
    @Parameter(name = "nombre", description = "Nombre del tipo de cuenta para la búsqueda", required = true)
    Optional<TipoCuenta> findByNombre(String nombre);

    /**
     * Busca un tipo de cuenta por su identificador GUID.
     *
     * @param guid Identificador global único del tipo de cuenta
     * @return Opcional que contiene el tipo de cuenta si existe
     */
    @Operation(summary = "Buscar un tipo de cuenta por su identificador GUID", description = "Devuelve un tipo de cuenta si existe un GUID coincidente.")
    @Parameter(name = "guid", description = "Identificador global único del tipo de cuenta", required = true)
    Optional<TipoCuenta> findByGuid(String guid);
}
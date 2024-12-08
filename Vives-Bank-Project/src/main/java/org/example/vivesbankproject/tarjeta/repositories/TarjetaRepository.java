package org.example.vivesbankproject.tarjeta.repositories;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para operaciones de persistencia y consulta de entidades Tarjeta.
 *
 * Extiende JpaRepository para operaciones CRUD básicas y JpaSpecificationExecutor
 * para consultas dinámicas y complejas.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Repository
@Tag(name = "Tarjeta Repository", description = "Repositorio para operaciones de gestión de tarjetas")
public interface TarjetaRepository extends JpaRepository<Tarjeta, Long>, JpaSpecificationExecutor<Tarjeta> {

    /**
     * Busca una tarjeta por su identificador único global (GUID).
     *
     * @param guid Identificador único global de la tarjeta
     * @return Un Optional que contiene la Tarjeta si se encuentra, o vacío si no existe
     */
    @Operation(
            summary = "Buscar tarjeta por GUID",
            description = "Recupera una tarjeta utilizando su identificador único global"
    )
    Optional<Tarjeta> findByGuid(String guid);

    /**
     * Busca una tarjeta por su número de tarjeta.
     *
     * @param numeroTarjeta Número único de la tarjeta
     * @return Un Optional que contiene la Tarjeta si se encuentra, o vacío si no existe
     */
    @Operation(
            summary = "Buscar tarjeta por número",
            description = "Recupera una tarjeta utilizando su número de tarjeta"
    )
    Optional<Tarjeta> findByNumeroTarjeta(String numeroTarjeta);
}
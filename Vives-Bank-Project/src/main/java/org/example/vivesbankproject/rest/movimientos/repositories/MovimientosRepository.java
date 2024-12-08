package org.example.vivesbankproject.rest.movimientos.repositories;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.rest.movimientos.models.Movimiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para realizar operaciones de acceso a la base de datos
 * relacionadas con la colección de movimientos en MongoDB.
 * Utiliza MongoRepository para interactuar con la base de datos NoSQL.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Repository
@Tag(name = "MovimientosRepository", description = "Repositorio para operaciones de base de datos con movimientos")
public interface MovimientosRepository extends MongoRepository<Movimiento, ObjectId> {

    /**
     * Busca un movimiento utilizando su identificador único (GUID).
     *
     * @param guid El identificador único del movimiento.
     * @return Un objeto Optional que contiene el movimiento si se encuentra.
     */
    @Operation(summary = "Buscar movimiento por GUID", description = "Obtiene un movimiento de la base de datos por su identificador único GUID")
    Optional<Movimiento> findByGuid(String guid);

    /**
     * Busca un movimiento utilizando el identificador del cliente.
     *
     * @param clienteId El identificador del cliente asociado al movimiento.
     * @return Un objeto Optional que contiene el movimiento si se encuentra.
     */
    @Operation(summary = "Buscar movimiento por cliente GUID", description = "Obtiene un movimiento de la base de datos utilizando el identificador del cliente")
    Optional<Movimiento> findByClienteGuid(String clienteId);
}
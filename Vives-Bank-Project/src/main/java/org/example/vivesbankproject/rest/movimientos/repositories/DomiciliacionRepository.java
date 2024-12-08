package org.example.vivesbankproject.rest.movimientos.repositories;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para realizar operaciones de acceso a la base de datos
 * relacionadas con la colección de domiciliaciones en MongoDB.
 * Utiliza MongoRepository para interactuar con la base de datos NoSQL.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Repository
@Tag(name = "DomiciliacionRepository", description = "Repositorio para operaciones de base de datos con domiciliaciones")
public interface DomiciliacionRepository extends MongoRepository<Domiciliacion, ObjectId> {

    /**
     * Encuentra una domiciliación por su identificador único (GUID).
     *
     * @param guid El identificador único de la domiciliación.
     * @return Un objeto Optional que contiene la domiciliación si se encuentra.
     */
    @Operation(summary = "Buscar domiciliación por GUID", description = "Obtiene una domiciliación de la base de datos por su identificador único GUID")
    Optional<Domiciliacion> findByGuid(String guid);

    /**
     * Busca una domiciliación utilizando el identificador del cliente.
     *
     * @param clienteGuid El identificador del cliente asociado a la domiciliación.
     * @return Un objeto Optional que contiene la domiciliación si se encuentra.
     */
    @Operation(summary = "Buscar domiciliación por cliente GUID", description = "Obtiene una domiciliación de la base de datos utilizando el GUID del cliente")
    Optional<Domiciliacion> findByClienteGuid(String clienteGuid);
}
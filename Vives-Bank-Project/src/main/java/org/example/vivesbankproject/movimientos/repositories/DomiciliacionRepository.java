package org.example.vivesbankproject.movimientos.repositories;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomiciliacionRepository extends MongoRepository<Domiciliacion, ObjectId> {
    Optional<Domiciliacion> findByGuid(String guid);

    Optional<Domiciliacion> findByClienteGuid(String clienteGuid);
}

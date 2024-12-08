package org.example.vivesbankproject.rest.tarjeta.repositories;

import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Long>, JpaSpecificationExecutor<Tarjeta> {
    Optional<Tarjeta> findByGuid(String guid);

    Optional<Tarjeta> findByNumeroTarjeta(String numeroTarjeta);
}
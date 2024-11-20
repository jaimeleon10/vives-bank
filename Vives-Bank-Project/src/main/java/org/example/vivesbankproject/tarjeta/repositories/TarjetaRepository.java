package org.example.vivesbankproject.tarjeta.repositories;

import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, UUID> {
}

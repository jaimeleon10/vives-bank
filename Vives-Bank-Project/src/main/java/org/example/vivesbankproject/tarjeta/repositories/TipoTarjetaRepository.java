package org.example.vivesbankproject.tarjeta.repositories;

import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TipoTarjetaRepository extends JpaRepository<TipoTarjeta, UUID> {
    Optional<TipoTarjeta> findByNombre(Tipo nombre);
}


package org.example.vivesbankproject.tarjeta.repositories;

import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, UUID>, JpaSpecificationExecutor<Tarjeta> {
}

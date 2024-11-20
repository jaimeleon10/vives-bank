package org.example.vivesbankproject.cuenta.repositories;

import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, UUID>, JpaSpecificationExecutor<Cuenta> {
    Optional<Cuenta> findByIban(String iban);
}
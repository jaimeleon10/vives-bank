package org.example.vivesbankproject.cuenta.repositories;

import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long>, JpaSpecificationExecutor<Cuenta> {
    Optional<Cuenta> findByGuid(String guid);
    Optional<Cuenta> findByIban(String iban);
}
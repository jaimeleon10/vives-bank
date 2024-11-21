package org.example.vivesbankproject.cuenta.repositories;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TipoCuentaRepository extends JpaRepository<TipoCuenta, String>, JpaSpecificationExecutor<TipoCuenta> {
    Optional<TipoCuenta> findByNombre(String nombre);
}
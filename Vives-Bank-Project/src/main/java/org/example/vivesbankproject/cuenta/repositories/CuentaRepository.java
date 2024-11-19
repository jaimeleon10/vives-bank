package org.example.vivesbankproject.cuenta.repositories;

import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CuentaRepository extends JpaRepository<Cuenta, UUID> {
}

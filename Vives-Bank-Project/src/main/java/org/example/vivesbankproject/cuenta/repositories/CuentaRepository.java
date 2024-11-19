package org.example.vivesbankproject.cuenta.repositories;

import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, UUID> {

}
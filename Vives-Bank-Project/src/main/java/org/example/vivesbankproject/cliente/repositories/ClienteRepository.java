package org.example.vivesbankproject.cliente.repositories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    Optional<Cliente> findByGuid(String guid);
    Optional<Cliente> findByDni(String dni);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByTelefono(String telefono);
    boolean existsByUserGuid(String userGuid);
    Optional<Cliente> findByUserGuid(String userGuid);
}
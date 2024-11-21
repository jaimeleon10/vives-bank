package org.example.vivesbankproject.cliente.repositories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String>, JpaSpecificationExecutor<Cliente> {
    Optional<Cliente> findByDni(String dni);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByTelefono(String telefono);
}
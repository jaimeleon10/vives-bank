package org.example.vivesbankproject.cuenta.repositories;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class TipoCuentaRepositoryTest {

    @Autowired
    private TipoCuentaRepository tipoCuentaRepository;

    private TipoCuenta tipoCuenta;

    @BeforeEach
    void setUp() {
        tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("Cuenta de Ahorros");
        tipoCuenta.setInteres(new BigDecimal("1.5"));
        tipoCuenta = tipoCuentaRepository.save(tipoCuenta);
    }

    @Test
    void findByNombre() {
        String nombre = "Cuenta de Ahorros";

        Optional<TipoCuenta> result = tipoCuentaRepository.findByNombre(nombre);

        assertTrue(result.isPresent());
        assertEquals(nombre, result.get().getNombre());
    }

    @Test
    void findByNombreNotFound() {
        String nombre = "Cuenta Inexistente";

        Optional<TipoCuenta> result = tipoCuentaRepository.findByNombre(nombre);

        assertFalse(result.isPresent());
    }

    @Test
    void findByGuid() {
        String guid = tipoCuenta.getGuid();

        Optional<TipoCuenta> result = tipoCuentaRepository.findByGuid(guid);

        assertTrue(result.isPresent());
        assertEquals(guid, result.get().getGuid());
    }

    @Test
    void findByGuidNotFound() {
        String guid = "non-existent-guid";

        Optional<TipoCuenta> result = tipoCuentaRepository.findByGuid(guid);

        assertFalse(result.isPresent());
    }

    @Test
    void FindAllConSpecification() {
        Pageable pageable = PageRequest.of(0, 10);

        Specification<TipoCuenta> spec = (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nombre"), "%Cuenta%");
        Page<TipoCuenta> result = tipoCuentaRepository.findAll(spec, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
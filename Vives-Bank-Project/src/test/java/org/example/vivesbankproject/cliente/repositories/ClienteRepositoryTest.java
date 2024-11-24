package org.example.vivesbankproject.cliente.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static User userTest;
    private static Cliente clienteTest;
    private static Cuenta cuenta1Test;
    private static Cuenta cuenta2Test;

    @BeforeEach
    void setUp() {
        userTest = User.builder()
                .guid("user-guid")
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        userTest = entityManager.merge(userTest);

        cuenta1Test = Cuenta.builder()
                .guid("cuenta1-guid")
                .iban("iban1")
                .saldo(BigDecimal.valueOf(100))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        cuenta2Test = Cuenta.builder()
                .guid("cuenta2-guid")
                .iban("iban2")
                .saldo(BigDecimal.valueOf(200))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        cuenta1Test = entityManager.merge(cuenta1Test);
        cuenta2Test = entityManager.merge(cuenta2Test);

        entityManager.flush();

        clienteTest = Cliente.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .user(userTest)
                .isDeleted(false)
                .cuentas(Set.of(cuenta1Test, cuenta2Test))
                .build();

        clienteTest = clienteRepository.saveAndFlush(clienteTest);
    }

    @Test
    void findByGuid() {
        var result = clienteRepository.findByGuid("unique-guid");

        assertAll(
                () -> assertEquals(clienteTest.getGuid(), result.get().getGuid()),
                () -> assertEquals(clienteTest.getDni(), result.get().getDni()),
                () -> assertEquals(clienteTest.getNombre(), result.get().getNombre()),
                () -> assertEquals(clienteTest.getApellidos(), result.get().getApellidos())
        );
    }

    @Test
    void findByGuidNotFound() {
        var result = clienteRepository.findByGuid("non-existent-guid");

        assertNull(result.orElse(null));
    }

    @Test
    void findByDni() {
        var result = clienteRepository.findByDni("12345678A");

        assertAll(
                () -> assertEquals(clienteTest.getDni(), result.get().getDni()),
                () -> assertEquals(clienteTest.getNombre(), result.get().getNombre()),
                () -> assertEquals(clienteTest.getApellidos(), result.get().getApellidos())
        );
    }

    @Test
    void findByEmail() {
        var result = clienteRepository.findByEmail("juan.perez@example.com");

        assertAll(
                () -> assertEquals(clienteTest.getEmail(), result.get().getEmail()),
                () -> assertEquals(clienteTest.getNombre(), result.get().getNombre()),
                () -> assertEquals(clienteTest.getApellidos(), result.get().getApellidos())
        );
    }

    @Test
    void findByTelefono() {
        var result = clienteRepository.findByTelefono("123456789");

        assertAll(
                () -> assertEquals(clienteTest.getTelefono(), result.get().getTelefono()),
                () -> assertEquals(clienteTest.getNombre(), result.get().getNombre()),
                () -> assertEquals(clienteTest.getApellidos(), result.get().getApellidos())
        );
    }

    @Test
    void existsByUserGuid() {
        boolean exists = clienteRepository.existsByUserGuid("user-guid");
        assertThat(exists).isTrue();
    }

    @Test
    void findCuentasAsignadas() {
        Set<String> cuentasIds = Set.of("cuenta1-guid", "cuenta2-guid");
        List<Cuenta> cuentas = clienteRepository.findCuentasAsignadas(cuentasIds);

        assertThat(cuentas).hasSize(2);
        assertThat(cuentas).extracting(Cuenta::getGuid).containsExactlyInAnyOrder("cuenta1-guid", "cuenta2-guid");
    }
}

package org.example.vivesbankproject.cliente.repositories;


import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.models.Direccion;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.utils.generators.IbanGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @MockBean
    private TestEntityManager entityManager;

    private User crearUsuario(String username) {
        return User.builder()
                .username(username)
                .password("securepassword")
                .roles(Set.of(Role.USER))
                .build();
    }

    private Cliente crearCliente(User user, String guid, String dni, String email, String telefono) {
        return Cliente.builder()
                .guid(guid)
                .dni(dni)
                .nombre("Nombre")
                .apellidos("Apellidos")
                .direccion(new Direccion("Calle Ejemplo", "1", "28001", "1", "A"))
                .email(email)
                .telefono(telefono)
                .fotoPerfil("fotoPerfil.jpg")
                .fotoDni("fotoDni.jpg")
                .user(user)
                .build();
    }

    private Cuenta crearCuenta(Cliente cliente, String guid, BigDecimal saldo) {
        return Cuenta.builder()
                .guid(guid)
                .iban(IbanGenerator.generateIban())
                .saldo(saldo)
                .cliente(cliente)
                .build();
    }

    @Test
    void FindByGuid() {
        User user = crearUsuario("testUser");
        user = entityManager.persistAndFlush(user);

        Cliente cliente = crearCliente(user, "guid-123", "12345678Z", "email@test.com", "123456789");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByGuid("guid-123");
        assertTrue(result.isPresent());
        assertEquals("guid-123", result.get().getGuid());
    }
    @Test
    void FindByDni() {
        User user = crearUsuario("testUserDni");
        entityManager.persist(user);

        Cliente cliente = crearCliente(user, "guid-dni", "87654321Y", "email@dni.com", "987654321");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByDni("87654321Y");
        assertTrue(result.isPresent());
        assertEquals("87654321Y", result.get().getDni());
    }

    @Test
    void FindByEmail() {
        User user = crearUsuario("testUserEmail");
        entityManager.persist(user);

        Cliente cliente = crearCliente(user, "guid-email", "45678912X", "email@domain.com", "456789123");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByEmail("email@domain.com");
        assertTrue(result.isPresent());
        assertEquals("email@domain.com", result.get().getEmail());
    }

    @Test
    void ExistsByUserGuid() {
        User user = crearUsuario("userExistCheck");
        entityManager.persist(user);

        Cliente cliente = crearCliente(user, "guid-user-check", "11223344Z", "user@exist.com", "112233445");
        cliente = clienteRepository.save(cliente);

        boolean exists = clienteRepository.existsByUserGuid(user.getGuid());
        assertTrue(exists);
    }

    @Test
    void FindCuentasAsignadas() {
        User user = crearUsuario("testUserCuentas");
        entityManager.persist(user);

        Cliente cliente = crearCliente(user, "guid-cuentas", "22334455A", "cuentas@test.com", "223344556");
        cliente = clienteRepository.save(cliente);

        Cuenta cuenta1 = crearCuenta(cliente, "cuenta-001", BigDecimal.valueOf(1000));
        Cuenta cuenta2 = crearCuenta(cliente, "cuenta-002", BigDecimal.valueOf(2000));

        entityManager.persist(cuenta1);
        entityManager.persist(cuenta2);

        List<Cuenta> cuentas = clienteRepository.findCuentasAsignadas(Set.of("cuenta-001", "cuenta-002"));
        assertEquals(2, cuentas.size());
        assertTrue(cuentas.stream().anyMatch(c -> c.getGuid().equals("cuenta-001")));
        assertTrue(cuentas.stream().anyMatch(c -> c.getGuid().equals("cuenta-002")));
    }

    @Test
    void FindByTelefono() {
        User user = crearUsuario("testUserTelefono");
        entityManager.persist(user);

        Cliente cliente = crearCliente(user, "guid-telefono", "66778899M", "telefono@test.com", "667788990");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByTelefono("667788990");
        assertTrue(result.isPresent());
        assertEquals("667788990", result.get().getTelefono());
    }
}
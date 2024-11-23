package org.example.vivesbankproject.cliente.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente;
    private Cuenta cuenta1;
    private Cuenta cuenta2;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();

        User user = User.builder()
                .guid("user-guid")
                .username("testuser")
                .password("password")
                .build();

        userRepository.save(user);

        cliente = Cliente.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .user(user)
                .isDeleted(false)
                .build();

        cuenta1 = Cuenta.builder().guid("cuenta1-guid").build();
        cuenta2 = Cuenta.builder().guid("cuenta2-guid").build();
        cliente.setCuentas(Set.of(cuenta1, cuenta2));

        clienteRepository.save(cliente);
    }


    @Test
    void FindByGuid() {
        Optional<Cliente> foundCliente = clienteRepository.findByGuid("unique-guid");
        assertThat(foundCliente).isPresent();
        assertThat(foundCliente.get().getGuid()).isEqualTo("unique-guid");
    }

    @Test
    void FindByDni() {
        Optional<Cliente> foundCliente = clienteRepository.findByDni("12345678A");
        assertThat(foundCliente).isPresent();
        assertThat(foundCliente.get().getDni()).isEqualTo("12345678A");
    }

    @Test
    void FindByEmail() {
        Optional<Cliente> foundCliente = clienteRepository.findByEmail("juan.perez@example.com");
        assertThat(foundCliente).isPresent();
        assertThat(foundCliente.get().getEmail()).isEqualTo("juan.perez@example.com");
    }

    @Test
    void FindByTelefono() {
        Optional<Cliente> foundCliente = clienteRepository.findByTelefono("123456789");
        assertThat(foundCliente).isPresent();
        assertThat(foundCliente.get().getTelefono()).isEqualTo("123456789");
    }

    @Test
    void ExistsByUserGuid() {
        boolean exists = clienteRepository.existsByUserGuid("user-guid");
        assertThat(exists).isTrue();
    }

    @Test
    void FindCuentasAsignadas() {
        Set<String> cuentasIds = Set.of("cuenta1-guid", "cuenta2-guid");
        List<Cuenta> cuentas = clienteRepository.findCuentasAsignadas(cuentasIds);
        assertThat(cuentas).hasSize(2);
        assertThat(cuentas).extracting(Cuenta::getGuid).containsExactlyInAnyOrder("cuenta1-guid", "cuenta2-guid");
    }
}

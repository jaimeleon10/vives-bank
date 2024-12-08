package org.example.vivesbankproject.cliente.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.Set;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.models.Direccion;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.users.models.Role;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    private User crearUsuario(String username) {
        User user = User.builder()
                .username(username)
                .password("securepassword")
                .roles(Set.of(Role.USER))
                .build();
        return userRepository.save(user);
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

    @Test
    void FindByGuid() {
        User user = crearUsuario("testUser");
        Cliente cliente = crearCliente(user, "guid-123", "12345678Z", "email@test.com", "123456789");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByGuid("guid-123");
        assertTrue(result.isPresent());
        assertEquals("guid-123", result.get().getGuid());
    }

    @Test
    void FindByDni() {
        User user = crearUsuario("testUserDni");
        Cliente cliente = crearCliente(user, "guid-dni", "87654321Y", "email@dni.com", "987654321");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByDni("87654321Y");
        assertTrue(result.isPresent());
        assertEquals("87654321Y", result.get().getDni());
    }

    @Test
    void FindByEmail() {
        User user = crearUsuario("testUserEmail");
        Cliente cliente = crearCliente(user, "guid-email", "45678912X", "email@domain.com", "456789123");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByEmail("email@domain.com");
        assertTrue(result.isPresent());
        assertEquals("email@domain.com", result.get().getEmail());
    }

    @Test
    void ExistsByUserGuid() {
        User user = crearUsuario("userExistCheck");
        Cliente cliente = crearCliente(user, "guid-user-check", "11223344Z", "user@exist.com", "112233445");
        cliente = clienteRepository.save(cliente);

        boolean exists = clienteRepository.existsByUserGuid(user.getGuid());
        assertTrue(exists);
    }

    @Test
    void FindByTelefono() {
        User user = crearUsuario("testUserTelefono");
        Cliente cliente = crearCliente(user, "guid-telefono", "66778899M", "telefono@test.com", "667788990");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByTelefono("667788990");
        assertTrue(result.isPresent());
        assertEquals("667788990", result.get().getTelefono());
    }

    @Test
    void FindByUserGuid() {
        User user = crearUsuario("testUserGuid");
        Cliente cliente = crearCliente(user, "guid-12345", "12345678A", "userguid@test.com", "123456789");
        cliente = clienteRepository.save(cliente);

        Optional<Cliente> result = clienteRepository.findByUserGuid(user.getGuid());
        assertTrue(result.isPresent());
        assertEquals(user.getGuid(), result.get().getUser().getGuid());
    }
}
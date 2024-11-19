package org.example.vivesbankproject.cuenta.services;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {
    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    private Cuenta cuentaTest;
    private Cliente clienteTest;
    private Tarjeta tarjetaTest;

    @BeforeEach
    void setUp() {
        clienteTest = new Cliente();
        clienteTest.setId(UUID.fromString("d7293a53-c441-4cda-aea2-230cbcf7ec27"));
        clienteTest.setDni("76742083F");
        clienteTest.setNombre("Juan");
        clienteTest.setApellidos("Pérez");
        clienteTest.setEmail("juan.perez@gmail.com");
        clienteTest.setTelefono("678349823");
        clienteTest.setFotoPerfil("https://via.placeholder.com/150");
        clienteTest.setFotoDni("https://via.placeholder.com/150");

        tarjetaTest = new Tarjeta();
        tarjetaTest.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjetaTest.setNumeroTarjeta("4242424242424242");
        tarjetaTest.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaTest.setCvv(123);
        tarjetaTest.setPin("1234");
        tarjetaTest.setLimiteDiario(100.0);
        tarjetaTest.setLimiteSemanal(200.0);
        tarjetaTest.setLimiteMensual(500.0);
        tarjetaTest.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        cuentaTest = new Cuenta();
        cuentaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        cuentaTest.setIban("ES9120804243448487618583");
        cuentaTest.setSaldo(1000.0);
        cuentaTest.setCliente(clienteTest);
        cuentaTest.setTarjeta(tarjetaTest);
        cuentaTest.setIsDeleted(false);
    }

    @Test
    void getAll() {
        List<Cuenta> cuentas = List.of(new Cuenta(), new Cuenta());
        Page<Cuenta> expectedPage = new PageImpl<>(cuentas);
        Pageable pageable = PageRequest.of(0, 10);

        when(cuentaRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Cuenta> result = cuentaService.getAll(pageable);

        assertAll(
                () -> assertEquals(expectedPage, result),
                () -> assertEquals(expectedPage.getContent(), result.getContent()),
                () -> assertEquals(expectedPage.getTotalElements(), result.getTotalElements())
        );

        verify(cuentaRepository, times(1)).findAll(pageable);
    }

    @Test
    void getById() {
        UUID idCuenta = cuentaTest.getId();
        Cuenta expectedCuenta = new Cuenta();
        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.of(expectedCuenta));

        Optional<Cuenta> resultCuenta = cuentaService.getById(idCuenta);

        assertEquals(expectedCuenta, resultCuenta);

        verify(cuentaRepository).findById(idCuenta);
    }

    @Test
    void getByIdNotFound() {
        UUID idCuenta = UUID.randomUUID();
        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.getById(idCuenta));

        verify(cuentaRepository).findById(idCuenta);
    }

    @Test
    void save() {
    }

    @Test
    void update() {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.fromString("d7293a53-c441-4cda-aea2-230cbcf7ec27"));
        cliente.setDni("76742083F");
        cliente.setNombre("Juan");
        cliente.setApellidos("Pérez");
        cliente.setEmail("juan.perez@gmail.com");
        cliente.setTelefono("678349823");
        cliente.setFotoPerfil("https://via.placeholder.com/150");
        cliente.setFotoDni("https://via.placeholder.com/150");

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjeta.setNumeroTarjeta("4242424242424242");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(123);
        tarjeta.setPin("1234");
        tarjeta.setLimiteDiario(100.0);
        tarjeta.setLimiteSemanal(200.0);
        tarjeta.setLimiteMensual(500.0);
        tarjeta.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        Cuenta cuenta = new Cuenta();
        cuenta.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        cuenta.setIban("ES9120804243448487618583");
        cuenta.setSaldo(1000.0);
        cuenta.setCliente(clienteTest);
        cuenta.setTarjeta(tarjetaTest);
        cuenta.setIsDeleted(false);

        UUID idCuenta = cuenta.getId();

        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.of(pedidoToUpdate));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(pedidoToUpdate);

        Cuenta resultPedido = cuentaService.update(idCuenta, cuenta);

        assertAll(
                () -> assertEquals(pedidoToUpdate, resultPedido),
                () -> assertEquals(pedidoToUpdate.getLineaPedido(), resultPedido.getLineaPedido()),
                () -> assertEquals(pedidoToUpdate.getLineaPedido().size(), resultPedido.size())
        );

        verify(cuentaRepository).findById(idCuenta);
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    void deleteById() {
    }
}
package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.services.CuentaServiceImpl;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.utils.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class CuentaControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    CuentaServiceImpl cuentaService;

    @Autowired
    MockMvc mvc;

    CuentaMapper cuentaMapper = new CuentaMapper();
    String myEndpoint = "/v1/cuentas";

    private Cuenta cuentaTest;
    private Cliente clienteTest;
    private Tarjeta tarjetaTest;
    private TipoCuenta tipoCuentaTest;

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

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(2.0);

        cuentaTest = new Cuenta();
        cuentaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        cuentaTest.setIban("ES9120804243448487618583");
        cuentaTest.setSaldo(1000.0);
        cuentaTest.setCliente(clienteTest);
        cuentaTest.setTipoCuenta(tipoCuentaTest);
        cuentaTest.setTarjeta(tarjetaTest);
        cuentaTest.setIsDeleted(false);

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        String iban = "ES9120804243448487618583";
        Double saldo = 1000.0;
        Cliente cliente = clienteTest;
        Tarjeta tarjeta = tarjetaTest;
        TipoCuenta tipoCuenta = tipoCuentaTest;

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Cuenta> cuentaPage = new PageImpl<>(List.of(cuentaTest));

        when(cuentaService.getAll(
                Optional.of(iban),
                Optional.of(saldo),
                Optional.of(cliente),
                Optional.of(tarjeta),
                Optional.of(tipoCuenta),
                pageRequest
        )).thenReturn(cuentaPage);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint)
                                .param("iban", iban)
                                .param("saldo", String.valueOf(saldo))
                                .param("cliente", String.valueOf(cliente))
                                .param("tarjeta", String.valueOf(tarjeta))
                                .param("tipoCuenta", String.valueOf(tipoCuenta))
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Cuenta> pageResponse = objectMapper.readValue(
                response.getContentAsString(),
                objectMapper.getTypeFactory().constructParametricType(PageResponse.class, Cuenta.class)
        );

        List<Cuenta> res = pageResponse.content();

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertFalse(res.isEmpty()),
                () -> assertTrue(res.stream().anyMatch(r -> r.getId().equals(cuentaTest.getId())))
        );

        verify(cuentaService, times(1)).getAll(
                Optional.of(iban),
                Optional.of(saldo),
                Optional.of(cliente),
                Optional.of(tarjeta),
                Optional.of(tipoCuenta),
                pageRequest
        );
    }

    @Test
    void getById() throws Exception {
        when(cuentaService.getById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"))).thenReturn(Optional.of(cuentaTest));

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint + "/12d45756-3895-49b2-90d3-c4a12d5ee081")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Cuenta res = objectMapper.readValue(response.getContentAsString(), Cuenta.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cuentaTest.getId(), res.getId()),
                () -> assertEquals(cuentaTest.getIban(), res.getIban()),
                () -> assertEquals(cuentaTest.getSaldo(), res.getSaldo()),
                () -> assertEquals(cuentaTest.getCliente(), res.getCliente()),
                () -> assertEquals(cuentaTest.getTarjeta(), res.getTarjeta()),
                () -> assertEquals(cuentaTest.getTipoCuenta(), res.getTipoCuenta())
        );

        verify(cuentaService, times(1)).getById(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
    }

    @Test
    void save() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.fromString("68aa261a-56d7-4e5f-a7b9-1b6e7b3a04a4"));
        cliente.setDni("44889646V");
        cliente.setNombre("Jesus");
        cliente.setApellidos("Jimenez");
        cliente.setEmail("jesus.jimenez@gmail.com");
        cliente.setTelefono("623479558");
        cliente.setFotoPerfil("https://via.placeholder.com/150");
        cliente.setFotoDni("https://via.placeholder.com/150");

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.fromString("7b498e86-5197-4e05-9361-3da894b62353"));
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(987);
        tarjeta.setPin("0987");
        tarjeta.setLimiteDiario(100.0);
        tarjeta.setLimiteSemanal(200.0);
        tarjeta.setLimiteMensual(500.0);

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(2.0);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(UUID.fromString("6c257ab6-e588-4cef-a479-c2f8fcd7379a"));
        cuenta.setIban("ES0901869615019736267715");
        cuenta.setSaldo(1000.0);
        cuenta.setCliente(cliente);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        when(cuentaService.save(cuenta)).thenReturn(cuentaMapper.toCuentaUpdate(cuenta));

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cuenta)))
                .andReturn().getResponse();

        Cuenta res = objectMapper.readValue(response.getContentAsString(), Cuenta.class);

        assertAll(
                () -> assertEquals( HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(cuentaMapper.toCuentaUpdate(cuenta).getId(), res.getId()),
                () -> assertEquals(cuenta.getIban(), res.getIban()),
                () -> assertEquals(cuenta.getSaldo(), res.getSaldo()),
                () -> assertEquals(cuenta.getCliente(), res.getCliente()),
                () -> assertEquals(cuenta.getTarjeta(), res.getTarjeta()),
                () -> assertEquals(cuenta.getTipoCuenta(), res.getTipoCuenta())
        );

        verify(cuentaService, times(1)).save(cuenta);
    }

    @Test
    void update() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.fromString("d7293a53-c441-4cda-aea2-230cbcf7ec27"));
        cliente.setDni("46911981P");
        cliente.setNombre("Pepe");
        cliente.setApellidos("Gómez");
        cliente.setEmail("pepe.gomez@gmail.com");
        cliente.setTelefono("601938475");
        cliente.setFotoPerfil("https://via.placeholder.com/150");
        cliente.setFotoDni("https://via.placeholder.com/150");

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(456);
        tarjeta.setPin("4567");
        tarjeta.setLimiteDiario(100.0);
        tarjeta.setLimiteSemanal(200.0);
        tarjeta.setLimiteMensual(500.0);
        tarjeta.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("ahorro");
        tipoCuentaTest.setInteres(3.0);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(UUID.fromString("6c257ab6-e588-4cef-a479-c2f8fcd7379a"));
        cuenta.setIban("ES7302413102733585086708");
        cuenta.setSaldo(1000.0);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        when(cuentaService.getById(cuenta.getId())).thenReturn(Optional.of(cuenta));
        when(cuentaService.save(any(Cuenta.class))).thenReturn(cuenta);

        MockHttpServletResponse response = mvc.perform(
                        put(myEndpoint + "/6c257ab6-e588-4cef-a479-c2f8fcd7379a")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cuenta)))
                .andReturn().getResponse();

        Cuenta res = objectMapper.readValue(response.getContentAsString(), Cuenta.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cuenta.getIban(), res.getIban()),
                () -> assertEquals(cuenta.getSaldo(), res.getSaldo()),
                () -> assertEquals(cuenta.getCliente(), res.getCliente()),
                () -> assertEquals(cuenta.getTarjeta(), res.getTarjeta()),
                () -> assertEquals(cuenta.getTipoCuenta(), res.getTipoCuenta())
        );

        verify(cuentaService).getById(cuenta.getId());
        verify(cuentaService).save(any(Cuenta.class));
    }

    @Test
    void delete() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.fromString("d7293a53-c441-4cda-aea2-230cbcf7ec27"));
        cliente.setDni("46911981P");
        cliente.setNombre("Pepe");
        cliente.setApellidos("Gómez");
        cliente.setEmail("pepe.gomez@gmail.com");
        cliente.setTelefono("601938475");
        cliente.setFotoPerfil("https://via.placeholder.com/150");
        cliente.setFotoDni("https://via.placeholder.com/150");

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(456);
        tarjeta.setPin("4567");
        tarjeta.setLimiteDiario(100.0);
        tarjeta.setLimiteSemanal(200.0);
        tarjeta.setLimiteMensual(500.0);
        tarjeta.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("ahorro");
        tipoCuentaTest.setInteres(3.0);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(UUID.fromString("6c257ab6-e588-4cef-a479-c2f8fcd7379a"));
        cuenta.setIban("ES7302413102733585086708");
        cuenta.setSaldo(1000.0);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        when(cuentaService.delete(UUID.fromString("6c257ab6-e588-4cef-a479-c2f8fcd7379a"))).thenReturn(cuenta);

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.delete(myEndpoint + "/6c257ab6-e588-4cef-a479-c2f8fcd7379a")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals("", response.getContentAsString());

        verify(cuentaService, times(1)).delete(UUID.fromString("6c257ab6-e588-4cef-a479-c2f8fcd7379a"));
    }
}
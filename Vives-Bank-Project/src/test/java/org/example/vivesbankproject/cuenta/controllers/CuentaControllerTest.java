package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.vivesbankproject.cliente.dto.ClienteForCuentaResponse;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CuentaControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CuentaService cuentaService;

    private final String myEndpoint = "/v1/cuentas";

    private Cuenta cuentaTest;
    private Tarjeta tarjetaTest;
    private TipoCuenta tipoCuentaTest;

    @BeforeEach
    void setUp() {
        tarjetaTest = new Tarjeta();
        tarjetaTest.setGuid("921f6b86-695d-4361-8905-365d97691024");
        tarjetaTest.setNumeroTarjeta("4242424242424242");
        tarjetaTest.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaTest.setCvv(123);
        tarjetaTest.setPin("1234");
        tarjetaTest.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjetaTest.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjetaTest.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjetaTest.setTipoTarjeta(TipoTarjeta.valueOf("DEBITO"));

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));

        cuentaTest = new Cuenta();
        cuentaTest.setGuid("12d45756-3895-49b2-90d3-c4a12d5ee081");
        cuentaTest.setIban("ES9120804243448487618583");
        cuentaTest.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaTest.setTipoCuenta(tipoCuentaTest);
        cuentaTest.setTarjeta(tarjetaTest);
        cuentaTest.setIsDeleted(false);

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        String iban = "ES9120804243448487618583";
        BigDecimal saldoMax = BigDecimal.valueOf(1000.0);
        BigDecimal saldoMin = BigDecimal.valueOf(500.0);
        String tipoCuenta = String.valueOf(tipoCuentaTest);

        TarjetaResponse tarjetaResponse = new TarjetaResponse();
        tarjetaResponse.setGuid("921f6b86-695d-4361-8905-365d97691024");
        tarjetaResponse.setNumeroTarjeta("4242424242424242");
        tarjetaResponse.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaResponse.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjetaResponse.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjetaResponse.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjetaResponse.setTipoTarjeta(TipoTarjeta.valueOf("DEBITO"));

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();
        tipoCuentaResponse.setNombre("normal");
        tipoCuentaResponse.setInteres(BigDecimal.valueOf(2.0));

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setGuid("12d45756-3895-49b2-90d3-c4a12d5ee081");
        cuentaResponse.setIban(iban);
        cuentaResponse.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaResponse.setTipoCuenta(tipoCuentaResponse);
        cuentaResponse.setTarjeta(tarjetaResponse);
        cuentaResponse.setIsDeleted(false);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<CuentaResponse> cuentaPage = new PageImpl<>(List.of(cuentaResponse));

        when(cuentaService.getAll(
                Optional.of(iban),
                Optional.of(saldoMax),
                Optional.of(saldoMin),
                Optional.of(tipoCuenta),
                pageRequest
        )).thenReturn(cuentaPage);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint)
                                .param("iban", iban)
                                .param("saldoMax", String.valueOf(saldoMax))
                                .param("saldoMin", String.valueOf(saldoMin))
                                .param("tipoCuenta", tipoCuenta)
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
                () -> assertTrue(res.stream().anyMatch(r -> r.getGuid() != null && r.getGuid().equals(cuentaResponse.getGuid()))),
                () -> assertEquals(res.size(), 1),
                () -> assertTrue(res.get(0).getGuid().equals(cuentaResponse.getGuid()))
        );

        verify(cuentaService, times(1)).getAll(
                Optional.of(iban),
                Optional.of(saldoMax),
                Optional.of(saldoMin),
                Optional.of(tipoCuenta),
                pageRequest
        );
    }

    @Test
    void getById() throws Exception {
        TarjetaResponse tarjetaResponse = new TarjetaResponse();
        tarjetaResponse.setGuid("921f6b86-695d-4361-8905-365d97691024");
        tarjetaResponse.setNumeroTarjeta("4242424242424242");
        tarjetaResponse.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaResponse.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjetaResponse.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjetaResponse.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjetaResponse.setTipoTarjeta(TipoTarjeta.valueOf("DEBITO"));

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();
        tipoCuentaResponse.setNombre("normal");
        tipoCuentaResponse.setInteres(BigDecimal.valueOf(2.0));

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setGuid("12d45756-3895-49b2-90d3-c4a12d5ee081");
        cuentaResponse.setIban("ES9120804243448487618583");
        cuentaResponse.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaResponse.setTarjeta(tarjetaResponse);
        cuentaResponse.setTipoCuenta(tipoCuentaResponse);
        cuentaResponse.setIsDeleted(false);

        when(cuentaService.getById("12d45756-3895-49b2-90d3-c4a12d5ee081")).thenReturn(cuentaResponse);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint + "/12d45756-3895-49b2-90d3-c4a12d5ee081")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        CuentaResponse res = objectMapper.readValue(response.getContentAsString(), CuentaResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cuentaResponse.getGuid(), res.getGuid()),
                () -> assertEquals(cuentaResponse.getIban(), res.getIban()),
                () -> assertEquals(cuentaResponse.getSaldo(), res.getSaldo()),
                () -> assertEquals(cuentaResponse.getTarjeta(), res.getTarjeta()),
                () -> assertEquals(cuentaResponse.getTipoCuenta(), res.getTipoCuenta())
        );

        verify(cuentaService, times(1)).getById("12d45756-3895-49b2-90d3-c4a12d5ee081");
    }

    /*@Test
    void save() throws Exception {

        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuentaId("tipo-cuenta-guid");
        cuentaRequest.setTarjetaId("tarjeta-guid");
        cuentaRequest.setClienteId("cliente-guid");

        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .guid("cuenta-guid")
                .build();

        when(cuentaService.save(any(CuentaRequest.class))).thenReturn(cuentaResponse);

        mvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").value("cuenta-guid"));
    }
    */

    @Test
    void update() throws Exception {
        Cuenta cuenta = new Cuenta();
        cuenta.setGuid("6c257ab6-e588-4cef-a479-c2f8fcd7379a");
        cuenta.setIban("ES7302413102733585086708");
        cuenta.setSaldo(BigDecimal.valueOf(3000.0));
        cuenta.setTarjeta(tarjetaTest);
        cuenta.setTipoCuenta(tipoCuentaTest);
        cuenta.setIsDeleted(false);

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setGuid("7b498e86-5197-4e05-9361-3da894b62353");
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(987);
        tarjeta.setPin("0987");
        tarjeta.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjeta.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjeta.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjeta.setTipoTarjeta(TipoTarjeta.valueOf("DEBITO"));

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(2.0));

        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuentaId(cuenta.getTipoCuenta().getGuid());
        cuentaRequest.setTarjetaId(cuenta.getTarjeta().getGuid());

        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setSaldo(cuenta.getSaldo());
        cuentaRequestUpdate.setTipoCuentaId(cuentaRequest.getTipoCuentaId());
        cuentaRequestUpdate.setTarjetaId(cuentaRequest.getTarjetaId());
        cuentaRequestUpdate.setIsDeleted(false);

        TarjetaResponse tarjetaResponse = new TarjetaResponse();
        tarjetaResponse.setGuid(tarjeta.getGuid());
        tarjetaResponse.setNumeroTarjeta(tarjeta.getNumeroTarjeta());
        tarjetaResponse.setFechaCaducidad(tarjeta.getFechaCaducidad());
        tarjetaResponse.setLimiteDiario(tarjeta.getLimiteDiario());
        tarjetaResponse.setLimiteSemanal(tarjeta.getLimiteSemanal());
        tarjetaResponse.setLimiteMensual(tarjeta.getLimiteMensual());
        tarjetaResponse.setTipoTarjeta(tarjeta.getTipoTarjeta());

        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse();
        tipoCuentaResponse.setNombre("normal");
        tipoCuentaResponse.setInteres(BigDecimal.valueOf(2.0));

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setGuid(cuenta.getGuid());
        cuentaResponse.setIban(cuenta.getIban());
        cuentaResponse.setSaldo(cuenta.getSaldo());
        cuentaResponse.setTarjeta(tarjetaResponse);
        cuentaResponse.setTipoCuenta(tipoCuentaResponse);
        cuentaResponse.setIsDeleted(false);

        when(cuentaService.update(cuenta.getGuid(), cuentaRequestUpdate)).thenReturn(cuentaResponse);

        MockHttpServletResponse response = mvc.perform(
                        put(myEndpoint + "/6c257ab6-e588-4cef-a479-c2f8fcd7379a")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cuentaRequestUpdate)))
                .andReturn().getResponse();

        String responseBody = response.getContentAsString();

        CuentaResponse res = objectMapper.readValue(responseBody, CuentaResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cuenta.getIban(), res.getIban()),
                () -> assertEquals(cuenta.getSaldo(), res.getSaldo()),
                () -> assertEquals(tarjetaResponse, res.getTarjeta()),
                () -> assertEquals(tipoCuentaResponse, res.getTipoCuenta())
        );

        verify(cuentaService, times(1)).update(cuenta.getGuid(), cuentaRequestUpdate);
    }

    @Test
    void delete() throws Exception {
        String cuentaId = "6c257ab6-e588-4cef-a479-c2f8fcd7379a";

        doNothing().when(cuentaService).deleteById(cuentaId);

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.patch(myEndpoint + "/" + cuentaId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus(), "El estado debe ser 204 No Content"),
                () -> assertEquals("", response.getContentAsString(), "El cuerpo de la respuesta debe estar vacío")
        );

        verify(cuentaService, times(1)).deleteById(cuentaId);
    }

   /* @Test
    void testMethodArgumentNotValidException() throws Exception {
        mvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoCuentaId\":\"\",\"tarjetaId\":\"tarjeta-guid\",\"clienteId\":\"cliente-guid\"}")) // Error por tipoCuentaId vacío
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.tipoCuentaId").value("must not be empty")); // Verificamos que el mensaje de error esté en la respuesta
    }

    @Test
    void testConstraintViolationException() throws Exception {
        mvc.perform(post("/api/v1/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoCuentaId\":\"tipo-cuenta-guid\",\"tarjetaId\":\"tarjeta-guid\",\"clienteId\":\"\"}")) // Error por clienteId vacío
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.clienteId").value("must not be empty")); // Verificamos que el mensaje de error esté en la respuesta
    }*/
}

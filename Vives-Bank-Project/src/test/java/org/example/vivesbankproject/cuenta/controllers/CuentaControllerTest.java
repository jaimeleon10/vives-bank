package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.services.CuentaService;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class CuentaControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    CuentaService cuentaService;

    @Autowired
    MockMvc mvc;

    CuentaMapper cuentaMapper = new CuentaMapper();
    String myEndpoint = "/v1/cuentas";

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

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Cuenta> cuentaPage = new PageImpl<>(List.of(cuentaTest));

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
                () -> assertTrue(res.stream().anyMatch(r -> r.getId().equals(cuentaTest.getId())))
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
        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setGuid(cuentaTest.getGuid());
        cuentaResponse.setIban(cuentaTest.getIban());
        cuentaResponse.setSaldo(cuentaTest.getSaldo());
        cuentaResponse.setTarjeta(tarjetaTest);
        cuentaResponse.setTipoCuenta(tipoCuentaTest);
        cuentaResponse.setIsDeleted(false);

        when(cuentaService.getById("12d45756-3895-49b2-90d3-c4a12d5ee081")).thenReturn(cuentaResponse);

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
                () -> assertEquals(cuentaTest.getTarjeta(), res.getTarjeta()),
                () -> assertEquals(cuentaTest.getTipoCuenta(), res.getTipoCuenta())
        );

        verify(cuentaService, times(1)).getById("12d45756-3895-49b2-90d3-c4a12d5ee081");
    }

    @Test
    void save() throws Exception {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setGuid("7b498e86-5197-4e05-9361-3da894b62353");
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(987);
        tarjeta.setPin("0987");
        tarjeta.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjeta.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjeta.setLimiteMensual(BigDecimal.valueOf(500.0));

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(2.0));

        Cuenta cuenta = new Cuenta();
        cuenta.setGuid("6c257ab6-e588-4cef-a479-c2f8fcd7379a");
        cuenta.setIban("ES0901869615019736267715");
        cuenta.setSaldo(BigDecimal.valueOf(1000.0));
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuenta(tipoCuenta);
        cuentaRequest.setTarjeta(tarjeta);

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setGuid(cuenta.getGuid());
        cuentaResponse.setIban(cuenta.getIban());
        cuentaResponse.setSaldo(cuenta.getSaldo());
        cuentaResponse.setTarjeta(tarjeta);
        cuentaResponse.setTipoCuenta(tipoCuenta);
        cuentaResponse.setIsDeleted(false);

        when(cuentaService.save(cuentaRequest)).thenReturn(cuentaResponse);

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cuenta)))
                .andReturn().getResponse();

        Cuenta res = objectMapper.readValue(response.getContentAsString(), Cuenta.class);

        assertAll(
                () -> assertEquals( HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(cuenta.getId(), res.getId()),
                () -> assertEquals(cuenta.getIban(), res.getIban()),
                () -> assertEquals(cuenta.getSaldo(), res.getSaldo()),
                () -> assertEquals(cuenta.getTarjeta(), res.getTarjeta()),
                () -> assertEquals(cuenta.getTipoCuenta(), res.getTipoCuenta())
        );

        verify(cuentaService, times(1)).save(cuentaRequest);
    }

    @Test
    void update() throws Exception {
        Cuenta cuenta = new Cuenta();
        cuenta.setGuid("6c257ab6-e588-4cef-a479-c2f8fcd7379a");
        cuenta.setIban("ES7302413102733585086708");
        cuenta.setSaldo(BigDecimal.valueOf(3000.0));
        cuenta.setTarjeta(tarjetaTest);
        cuenta.setTipoCuenta(tipoCuentaTest);
        cuenta.setIsDeleted(false);

        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setTipoCuenta(cuenta.getTipoCuenta());
        cuentaRequest.setTarjeta(cuenta.getTarjeta());

        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setSaldo(cuenta.getSaldo());
        cuentaRequestUpdate.setTipoCuenta(cuentaRequest.getTipoCuenta());
        cuentaRequestUpdate.setTarjeta(cuentaRequest.getTarjeta());
        cuentaRequestUpdate.setIsDeleted(false);

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setGuid(cuenta.getGuid());
        cuentaResponse.setIban(cuenta.getIban());
        cuentaResponse.setSaldo(cuenta.getSaldo());
        cuentaResponse.setTarjeta(cuenta.getTarjeta());
        cuentaResponse.setTipoCuenta(cuenta.getTipoCuenta());
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
                () -> assertEquals(cuenta.getTarjeta(), res.getTarjeta()),
                () -> assertEquals(cuenta.getTipoCuenta(), res.getTipoCuenta())
        );

        verify(cuentaService, times(1)).update(cuenta.getGuid(), cuentaRequestUpdate);
    }


    @Test
    void delete() throws Exception {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setGuid("921f6b86-695d-4361-8905-365d97691024");
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(456);
        tarjeta.setPin("4567");
        tarjeta.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjeta.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjeta.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjeta.setTipoTarjeta(TipoTarjeta.valueOf("DEBITO"));

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("ahorro");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(3.0));

        Cuenta cuenta = new Cuenta();
        cuenta.setGuid("6c257ab6-e588-4cef-a479-c2f8fcd7379a");
        cuenta.setIban("ES7302413102733585086708");
        cuenta.setSaldo(BigDecimal.valueOf(1000.0));
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        when(cuentaService.delete("6c257ab6-e588-4cef-a479-c2f8fcd7379a")).thenReturn(cuenta);

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.delete(myEndpoint + "/6c257ab6-e588-4cef-a479-c2f8fcd7379a")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals("", response.getContentAsString());

        verify(cuentaService, times(1)).delete("6c257ab6-e588-4cef-a479-c2f8fcd7379a");
    }
}
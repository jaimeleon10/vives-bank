package org.example.vivesbankproject.cuenta.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.vivesbankproject.cuenta.dto.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.CuentaResponse;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.cuenta.services.TipoCuentaService;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.utils.PageResponse;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class TipoCuentaControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    TipoCuentaService tipoCuentaService;

    @Autowired
    MockMvc mvc;

    String myEndpoint = "/v1/tipocuentas";

    private TipoCuenta tipoCuentaTest;

    @BeforeEach
    void setUp() {
        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setGuid("hola");
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllPageable() throws Exception {
        String nombre = "normal";
        BigDecimal interes = BigDecimal.valueOf(2.0);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<TipoCuenta> cuentaPage = new PageImpl<>(List.of(tipoCuentaTest));

        when(tipoCuentaService.getAll(
                Optional.of(nombre),
                Optional.of(interes),
                pageRequest
        )).thenReturn(cuentaPage);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint)
                                .param("normal", nombre)
                                .param("interes", String.valueOf(interes))
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<TipoCuenta> pageResponse = objectMapper.readValue(
                response.getContentAsString(),
                objectMapper.getTypeFactory().constructParametricType(PageResponse.class, TipoCuenta.class)
        );

        List<TipoCuenta> res = pageResponse.content();

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertFalse(res.isEmpty()),
                () -> assertTrue(res.stream().anyMatch(r -> r.getId().equals(tipoCuentaTest.getId())))
        );

        verify(tipoCuentaService, times(1)).getAll(
                Optional.of(nombre),
                Optional.of(interes),
                pageRequest
        );
    }

    @Test
    void getById() throws Exception {
        when(tipoCuentaService.getById("hola")).thenReturn(tipoCuentaTest);

        MockHttpServletResponse response = mvc.perform(
                        get(myEndpoint + "/hola")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        TipoCuenta res = objectMapper.readValue(response.getContentAsString(), TipoCuenta.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(tipoCuentaTest.getId(), res.getId()),
                () -> assertEquals(tipoCuentaTest.getNombre(), res.getNombre()),
                () -> assertEquals(tipoCuentaTest.getInteres(), res.getInteres())
        );

        verify(tipoCuentaService, times(1)).getById("hola");
    }

    @Test
    void save() throws Exception {
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid("hola");
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(2.0));

        when(tipoCuentaService.save(tipoCuenta)).thenReturn(tipoCuenta);

        MockHttpServletResponse response = mvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tipoCuenta)))
                .andReturn().getResponse();

        TipoCuenta res = objectMapper.readValue(response.getContentAsString(), TipoCuenta.class);

        assertAll(
                () -> assertEquals( HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(tipoCuenta.getGuid(), res.getGuid()),
                () -> assertEquals(tipoCuenta.getNombre(), res.getNombre()),
                () -> assertEquals(tipoCuenta.getInteres(), res.getInteres())
        );

        verify(tipoCuentaService, times(1)).save(tipoCuenta);
    }

    @Test
    void update() throws Exception {
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid("adios");
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(BigDecimal.valueOf(3.0));

        when(tipoCuentaService.update(tipoCuenta.getGuid(), tipoCuenta)).thenReturn(tipoCuenta);

        MockHttpServletResponse response = mvc.perform(
                        put(myEndpoint + "/adios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tipoCuenta)))
                .andReturn().getResponse();

        String responseBody = response.getContentAsString();

        TipoCuenta res = objectMapper.readValue(responseBody, TipoCuenta.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(tipoCuenta.getNombre(), res.getNombre()),
                () -> assertEquals(tipoCuenta.getInteres(), res.getInteres())
        );

        verify(tipoCuentaService, times(1)).update(tipoCuenta.getGuid(), tipoCuenta);
    }

    @Test
    void delete() throws Exception {
        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setGuid("6c257ab6-e588-4cef-a479-c2f8fcd7379a");
        tipoCuenta.setNombre("ahorro");
        tipoCuenta.setInteres(BigDecimal.valueOf(3.0));

        doNothing().when(tipoCuentaService).deleteById("6c257ab6-e588-4cef-a479-c2f8fcd7379a");

        MockHttpServletResponse response = mvc.perform(
                        MockMvcRequestBuilders.delete(myEndpoint + "/6c257ab6-e588-4cef-a479-c2f8fcd7379a")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertEquals("", response.getContentAsString());

        verify(tipoCuentaService, times(1)).deleteById("6c257ab6-e588-4cef-a479-c2f8fcd7379a");
    }

}
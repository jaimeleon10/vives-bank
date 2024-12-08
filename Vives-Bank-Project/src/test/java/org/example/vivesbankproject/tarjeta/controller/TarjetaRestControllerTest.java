package org.example.vivesbankproject.tarjeta.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.validation.ConstraintViolationException;
import org.example.vivesbankproject.config.websockets.WebSocketConfig;
import org.example.vivesbankproject.rest.tarjeta.controller.TarjetaRestController;
import org.example.vivesbankproject.rest.tarjeta.dto.*;
import org.example.vivesbankproject.rest.tarjeta.repositories.TarjetaRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.example.vivesbankproject.rest.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class TarjetaRestControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WebSocketConfig webSocketConfig;

    @MockBean
    private TarjetaService tarjetaService;

    @MockBean
    private PaginationLinksUtils paginationLinksUtils;

    private TarjetaResponse tarjetaResponse;
    private TarjetaRestController tarjetaRestController;
    private TarjetaRequestSave tarjetaRequestSave;
    private TarjetaRequestUpdate tarjetaRequestUpdate;
    private MockHttpServletRequest request;
    private TarjetaRepository tarjetaRepository;

    @BeforeEach
    void setUp() {
        tarjetaRepository.deleteAll();
        MockitoAnnotations.openMocks(this);
        tarjetaRestController = new TarjetaRestController(tarjetaService, paginationLinksUtils);
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        tarjetaResponse = TarjetaResponse.builder()
                .guid("idTest")
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(String.valueOf(LocalDate.now().plusYears(5)))
                .limiteDiario("1000")
                .limiteSemanal("5000")
                .limiteMensual("20000")
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .createdAt("2024-11-26T15:23:45.123")
                .updatedAt("2024-11-26T15:23:45.123")
                .isDeleted(false)
                .build();

        tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("123")
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        tarjetaRequestUpdate = TarjetaRequestUpdate.builder()
                .limiteDiario(new BigDecimal("2000"))
                .limiteSemanal(new BigDecimal("10000"))
                .limiteMensual(new BigDecimal("40000"))
                .isDeleted(false)
                .build();
    }


    @Test
    void getAllDevuelvePageResponse() {
        List<TarjetaResponse> tarjetas = List.of(tarjetaResponse);
        Page<TarjetaResponse> page = new PageImpl<>(tarjetas);
        when(tarjetaService.getAll(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("");

        ResponseEntity<PageResponse<TarjetaResponse>> response = tarjetaRestController.getAll(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                0, 10, "id", "asc", request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tarjetaService).getAll(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void getTarjetaById() {
        when(tarjetaService.getById(anyString())).thenReturn(tarjetaResponse);

        ResponseEntity<TarjetaResponse> response = tarjetaRestController.getById("idTest");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tarjetaResponse, response.getBody());
        verify(tarjetaService).getById("idTest");
    }

    @Test
    void saveTarjeta() {
        when(tarjetaService.save(any(TarjetaRequestSave.class))).thenReturn(tarjetaResponse);

        ResponseEntity<TarjetaResponse> response = tarjetaRestController.save(tarjetaRequestSave);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tarjetaResponse, response.getBody());
        verify(tarjetaService).save(tarjetaRequestSave);
    }

    @Test
    void updateTarjeta() {
        when(tarjetaService.update(anyString(), any(TarjetaRequestUpdate.class))).thenReturn(tarjetaResponse);

        ResponseEntity<TarjetaResponse> response = tarjetaRestController.update("idTest", tarjetaRequestUpdate);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tarjetaResponse, response.getBody());
        verify(tarjetaService).update("idTest", tarjetaRequestUpdate);
    }

    @Test
    void deleteTarjeta() {
        doNothing().when(tarjetaService).deleteById(anyString());

        ResponseEntity<TarjetaResponse> response = tarjetaRestController.delete("idTest");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(tarjetaService).deleteById("idTest");
    }

    @Test
    void handleValidationExceptionError() throws Exception {
        var result = mockMvc.perform(post("/v1/tarjetas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pin\": \"\", \"limiteDiario\": 1000, \"limiteSemanal\": 5000, \"limiteMensual\": 20000, \"tipoTarjeta\": \"CREDITO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.pin").value("El PIN debe ser un numero de 4 digitos"))

                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println(responseContent);

        assertAll(
                () -> assertTrue(responseContent.contains("\"pin\""))
        );
    }


    @Test
    void getAllTarjetasConfiltros() {
        List<TarjetaResponse> tarjetas = List.of(tarjetaResponse);
        Page<TarjetaResponse> page = new PageImpl<>(tarjetas);
        when(tarjetaService.getAll(
                any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(PageRequest.class)
        )).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(any(), any())).thenReturn("");

        ResponseEntity<PageResponse<TarjetaResponse>> response = tarjetaRestController.getAll(
                Optional.of("1234567890123456"),
                Optional.of(LocalDate.now()),
                Optional.of(TipoTarjeta.CREDITO),
                Optional.of(new BigDecimal("500")),
                Optional.of(new BigDecimal("1500")),
                Optional.of(new BigDecimal("2500")),
                Optional.of(new BigDecimal("7500")),
                Optional.of(new BigDecimal("15000")),
                Optional.of(new BigDecimal("25000")),
                0, 10, "id", "desc", request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(tarjetaService).getAll(
                any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(PageRequest.class)
        );
    }
    @Test
    void pinVacio() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("")
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertEquals("El PIN debe ser un numero de 4 digitos", JsonPath.read(result.getResponse().getContentAsString(), "$.pin"))
        );
    }

    @Test
    void pinNull() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin(null)
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedErrorMessage = "El PIN debe ser un numero de 4 digitos";

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains(expectedErrorMessage))
        );
    }

    @Test
    void pinMuyCorto() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("12")
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El PIN debe ser un numero de 4 digitos"))
        );
    }

    @Test
    void pinConLetras() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("12A")
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El PIN debe ser un numero de 4 digitos"))
        );
    }

    @Test
    void limiteNegativoDiario() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("1234")
                .limiteDiario(new BigDecimal("-1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El limite diario debe ser un numero positivo"))
        );
    }

    @Test
    void limiteNegativoSemanal() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("1234")
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("-5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El limite semanal debe ser un numero positivo"))
        );
    }

    @Test
    void limiteNegativoMensual() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("1234")
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("-20000"))
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El limite mensual debe ser un numero positivo"))
        );
    }

    @Test
    void tipoTarjetaNull() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("1234")
                .limiteDiario(new BigDecimal("1000"))
                .limiteSemanal(new BigDecimal("5000"))
                .limiteMensual(new BigDecimal("20000"))
                .tipoTarjeta(null)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El tipo de tarjeta no puede ser un campo nulo"))
        );
    }

    @Test
    void sinLimit() throws Exception {
        TarjetaRequestSave tarjetaRequestSave = TarjetaRequestSave.builder()
                .pin("1234")
                .limiteDiario(BigDecimal.ZERO)
                .limiteSemanal(BigDecimal.ZERO)
                .limiteMensual(BigDecimal.ZERO)
                .tipoTarjeta(TipoTarjeta.CREDITO)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/tarjetas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tarjetaRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El limite diario debe ser un numero positivo")),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El limite semanal debe ser un numero positivo")),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El limite mensual debe ser un numero positivo"))
        );
    }

    @Test
    void getPrivateDataWithValidCredentials() {
        String cardId = "testCardId";
        TarjetaRequestPrivado requestPrivado = TarjetaRequestPrivado.builder()
                .username("validUser")
                .userPass("validPassword")
                .build();

        TarjetaResponsePrivado expectedResponse = TarjetaResponsePrivado.builder()
                .pin("1234")
                .cvv("123")
                .build();

        when(tarjetaService.getPrivateData(cardId, requestPrivado)).thenReturn(expectedResponse);

        ResponseEntity<TarjetaResponsePrivado> response = tarjetaRestController.getPrivateData(cardId, requestPrivado);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(tarjetaService).getPrivateData(cardId, requestPrivado);
    }

    @Test
    void getPrivateDataWithEmptyUsername() throws Exception {
        // Arrange
        String cardId = "testCardId";
        TarjetaRequestPrivado requestPrivado = TarjetaRequestPrivado.builder()
                .username("")
                .userPass("validPassword")
                .build();

        // Act & Assert
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/v1/tarjetas/{id}/private", cardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestPrivado)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El usuario no puede estar vacio"))
        );
    }

    @Test
    void getPrivateDataWithNullUsername() throws Exception {
        String cardId = "testCardId";
        TarjetaRequestPrivado requestPrivado = TarjetaRequestPrivado.builder()
                .username(null)
                .userPass("validPassword")
                .build();

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/v1/tarjetas/{id}/private", cardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestPrivado)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El usuario no puede estar vacio"))
        );
    }

    @Test
    void getPrivateDataWithEmptyPassword() throws Exception {
        String cardId = "testCardId";
        TarjetaRequestPrivado requestPrivado = TarjetaRequestPrivado.builder()
                .username("validUser")
                .userPass("")
                .build();

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/v1/tarjetas/{id}/private", cardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestPrivado)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La contraseña no puede estar vacia"))
        );
    }

    @Test
    void getPrivateDataWithNullPassword() throws Exception {
        String cardId = "testCardId";
        TarjetaRequestPrivado requestPrivado = TarjetaRequestPrivado.builder()
                .username("validUser")
                .userPass(null)
                .build();

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/v1/tarjetas/{id}/private", cardId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestPrivado)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La contraseña no puede estar vacia"))
        );
    }

    @Test
    void testHandleConstraintViolationException_EmptyViolations() {
        ConstraintViolationException mockException = mock(ConstraintViolationException.class);

        when(mockException.getConstraintViolations()).thenReturn(Set.of());

        Map<String, String> errors = tarjetaRestController.handleValidationExceptions(mockException);

        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }
}
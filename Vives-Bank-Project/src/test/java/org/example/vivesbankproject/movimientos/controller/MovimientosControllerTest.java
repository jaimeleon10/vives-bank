package org.example.vivesbankproject.movimientos.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.example.vivesbankproject.movimientos.services.MovimientosService;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.utils.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class MovimientosControllerTest {

    private static final String ENDPOINT = "${api.version}/movimientos";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MovimientosService movimientosService;

    @Autowired
    private JacksonTester<Movimientos> jsonMovimiento;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public MovimientosControllerTest(MovimientosService movimientosService) {
        this.movimientosService = movimientosService;
        mapper.registerModule(new JavaTimeModule());
    }

    private Cliente cliente;

    private Movimientos movimiento;

    private ObjectId movimientoId;

    @BeforeEach
    void setUp() {

        movimientoId = new ObjectId();

        cliente = Cliente.builder()
                .id(UUID.fromString("5f8761020988676500000001"))
                .dni("12345678A")
                .nombre("John")
                .apellidos("Doe")
                .email("john.doe@example.com")
                .telefono("123456789")
                .fotoPerfil("perfil.jpg")
                .fotoDni("dni.jpg")
                .cuentas(Set.of())
                .user(new User())
                .idMovimientos(movimientoId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        movimiento = Movimientos.builder()
                .id(movimientoId)
                .idUsuario(UUID.randomUUID())
                .cliente(cliente)
                .transacciones(new ArrayList<>())
                .isDeleted(false)
                .totalItems(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

    @Test
    void getMovimientos() throws Exception {
        var movimientosList = List.of(movimiento);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(movimientosList);

        when(movimientosService.getAll(any(Pageable.class))).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Movimientos> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(movimientosService, times(1)).getAll(any(Pageable.class));

    }

    @Test
    void getMovimientoById() {
    }

    @Test
    void getMovimientoByClienteId() {
    }

    @Test
    void createOrUpdateMovimientos() {
    }
}
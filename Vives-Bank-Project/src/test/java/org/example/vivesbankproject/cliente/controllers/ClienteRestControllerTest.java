package org.example.vivesbankproject.cliente.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class ClienteRestControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String myEndpoint = "/v1/cliente";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;


    @MockBean
    private PaginationLinksUtils paginationLinksUtils;


    @Test
    void getAll() throws Exception {
        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("userId")
                .createdAt("2024-11-26T15:23:45.123")
                .isDeleted(false)
                .build();

        Page<ClienteResponse> page = new PageImpl<>(List.of(clienteResponse), PageRequest.of(0, 10), 1);
        when(clienteService.getAll(any(), any(), any(), any(), any(), any())).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(eq(page), any())).thenReturn("");

        mockMvc.perform(get("/v1/cliente")
                        .param("dni", "12345678A")
                        .param("nombre", "Juan")
                        .param("apellido", "Perez")
                        .param("email", "juan.perez@example.com")
                        .param("telefono", "123456789")
                        .param("fotoPerfil","fotoprfil.jpg")
                        .param ("fotoDni","fotodni.jpg")
                        .param ("userId","userId")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dni").value("12345678A"))
                .andExpect(jsonPath("$.content[0].nombre").value("Juan"))
                .andExpect(jsonPath("$.content[0].apellidos").value("Perez"))
                .andExpect(jsonPath("$.content[0].email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.content[0].telefono").value("123456789"))
                .andExpect(jsonPath("$.content[0].fotoPerfil").value("fotoprfil.jpg"))
                .andExpect(jsonPath("$.content[0].fotoDni").value("fotodni.jpg"))
                .andExpect(jsonPath("$.content[0].isDeleted").value(false));
    }


    @Test
    void GetById() throws Exception {
        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        when(clienteService.getById("unique-guid")).thenReturn(clienteResponse);

        mockMvc.perform(get("/v1/cliente/unique-guid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"));
    }
    


    @Test
    void Save() throws Exception {
        CuentaRequest clienteRequestSave = CuentaRequest.builder()
                .tipoCuentaId("tipoCuentaId")
                .tarjetaId("tarjetaId")
                .clienteId("clienteId")
                .build();

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        when(clienteService.save(any(ClienteRequestSave.class))).thenReturn(clienteResponse);

        mockMvc.perform(post("/v1/cliente")
                        .contentType("application/json")
                        .content("{ \"dni\": \"12345678A\", \"nombre\": \"Juan\", \"apellidos\": \"Perez\", \"email\": \"juan.perez@example.com\", \"telefono\": \"123456789\" , \"fotoPerfil\": \"fotoprfil.jpg\", \"fotoDni\": \"fotodni.jpg\", \"cuentasIds\": [\"123456789\"], \"userId\": \"123456789\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"));
    }


    @Test
    void InvalidDni() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("1234567A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El DNI debe tener 8 numeros seguidos de una letra"))
        );
    }

    @Test
    void EmptyNombre() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El nombre no puede estar vacio"))
        );
    }

    @Test
    void EmptyApellidos() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("Juan")
                .apellidos("")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("Los apellidos no pueden estar vacio"))
        );
    }

    @Test
    void InvalidEmail() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perezexamplecom")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El email debe ser valido"))
        );
    }

    @Test
    void EmptyEmail() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("Juan")
                .apellidos("Perez")
                .email("")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .isDeleted(false)
                .build();

        MvcResult result = mockMvc.perform(
                        post("/v1/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El email no puede estar vacio"))
        );
    }

    @Test
    void InvalidTelefono() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("12345678")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .isDeleted(false)
                .build();

        MockHttpServletResponse result = mockMvc.perform(
                        post("/v1/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus()),
                () -> assertTrue(result.getContentAsString().contains("El telefono debe tener 9 numeros"))
        );
    }

    @Test
    void EmptyTelefono() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .isDeleted(false)
                .build();

        MockHttpServletResponse result = mockMvc.perform(
                        post("/v1/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestSave)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus()),
                () -> assertEquals("El telefono no puede estar vacio", result.getContentAsString())
        );
    }


    @Test
    void Update() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("123456789")
                .build();

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        when(clienteService.update(eq("unique-guid"), any(ClienteRequestUpdate.class))).thenReturn(clienteResponse);

        mockMvc.perform(put("/v1/cliente/unique-guid")
                        .contentType("application/json")
                        .content("{ \"nombre\": \"Juan\", \"apellidos\": \"Perez\", \"email\": \"juan.perez@example.com\", \"telefono\": \"123456789\", \"fotoPerfil\": \"fotoprfil.jpg\", \"fotoDni\": \"fotodni.jpg\", \"userId\": \"123456789\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"));
    }


    @Test
    void emptyNombreUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El nombre no puede estar vacio"))
        );
    }

    @Test
    void emptyApellidosUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("Los apellidos no pueden estar vacio"))
        );
    }

    @Test
    void invalidEmailUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perezexample.com") // Invalid email
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El email debe ser valido"))
        );
    }

    @Test
    void emptyEmailUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El email no puede estar vacio"))
        );
    }

    @Test
    void invalidTelefonoUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("12345678")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(responseContent.contains("El telefono debe tener 9 numeros"))
        );
    }

    @Test
    void emptyTelefonoUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(responseContent.contains("El telefono no puede estar vacio"))
        );
    }

    @Test
    void emptyFotoPerfilUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("")
                .fotoDni("fotodni.jpg")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La foto de perfil no puede estar vacia"))
        );
    }

    @Test
    void emptyFotoDniUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("")
                .userId("user-guid")
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("La foto del DNI no puede estar vacia"))
        );
    }

    @Test
    void emptyUserIdUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .userId("") // Empty userId
                .build();

        MvcResult result = mockMvc.perform(
                        put("/v1/cliente/{id}", "123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus()),
                () -> assertTrue(result.getResponse().getContentAsString().contains("El id de usuario no puede estar vacio"))
        );
    }


    @Test
    void Delete() throws Exception {
        Mockito.doNothing().when(clienteService).deleteById("unique-guid");

        mockMvc.perform(delete("/v1/cliente/unique-guid"))
                .andExpect(status().isNoContent());
    }

    /*@Test
    public void GetUserProfile() throws Exception {
        String token = jwtTokenUtil.generateToken("testUser", "userId-123", "USER");

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("user-guid-123")
                .dni("12345678A")
                .nombre("Test")
                .apellidos("User")
                .email("test@example.com")
                .telefono("123456789")
                .fotoPerfil("http://example.com/foto.jpg")
                .fotoDni("http://example.com/fotoDni.jpg")
                .userId("userId-123")
                .createdAt("2024-11-27T00:00:00")
                .updatedAt("2024-11-27T00:00:00")
                .isDeleted(false)
                .build();

        when(clienteService.getUserByGuid("user-guid-123")).thenReturn(clienteResponse);

        mockMvc.perform(get("/api/v1/usuario/me/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value("user-guid-123"))
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Test"))
                .andExpect(jsonPath("$.apellidos").value("User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"))
                .andExpect(jsonPath("$.fotoPerfil").value("http://example.com/foto.jpg"))
                .andDo(print());
    }*/

    @Test
    void handleValidationExceptionUpdateError() throws Exception {
        var result = mockMvc.perform(put("/v1/cliente/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"\", \"apellidos\": \"\", \"email\": \"\", \"telefono\": \"\" }")) // Campos vacíos
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre no puede estar vacio"))
                .andExpect(jsonPath("$.apellidos").value("Los apellidos no pueden estar vacio"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println(responseContent);

        assertAll(
                () -> assertTrue(responseContent.contains("\"email\":\"El email no puede estar vacio\"")
                        || responseContent.contains("\"email\":\"El email debe ser valido\"")),
                () -> assertTrue(responseContent.contains("\"telefono\":\"El telefono no puede estar vacio\"")
                        || responseContent.contains("\"telefono\":\"El telefono debe tener 9 numeros\""))
        );
    }
}
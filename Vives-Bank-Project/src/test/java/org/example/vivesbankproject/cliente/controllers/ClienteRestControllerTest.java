package org.example.vivesbankproject.cliente.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolationException;
import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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
                .user(UserResponse.builder().guid("user-guid").username("testuser").build())
                .createdAt(LocalDateTime.now())
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
                .andExpect(jsonPath("$.content[0].user.guid").value("user-guid"))
                .andExpect(jsonPath("$.content[0].user.username").value("testuser"))
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
    void CreateCliente() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .cuentasIds(Set.of("123456789"))
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
                .dni("1234567A") // Invalid DNI
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .cuentasIds(new HashSet<>())
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
                .nombre("") // Empty nombre
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .cuentasIds(new HashSet<>())
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
                .apellidos("") // Empty apellidos
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .cuentasIds(new HashSet<>())
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
                .email("juan.perezexamplecom") // Invalid email
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .cuentasIds(new HashSet<>())
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

   /* @Test
    void EmptyEmail() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("Juan")
                .apellidos("Perez")
                .email("") // Empty email
                .telefono("123456789")
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .cuentasIds(new HashSet<>())
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
    }*/

    @Test
    void InvalidTelefono() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("12345678") // Invalid telefono
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .cuentasIds(new HashSet<>())
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

    /*@Test
    void EmptyTelefono() throws Exception {
        ClienteRequestSave clienteRequestSave = ClienteRequestSave.builder()
                .dni("12345678Z")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("") // Empty telefono
                .fotoPerfil("fotoprfil.jpg")
                .fotoDni("fotodni.jpg")
                .cuentasIds(new HashSet<>())
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
    }*/


    @Test
    void UpdateCliente() throws Exception {
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
                .nombre("") // Empty nombre
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
                .apellidos("") // Empty apellidos
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
                () -> assertTrue(result.getResponse().getContentAsString().contains("El email debe ser valido")),
                () -> assertFalse(result.getResponse().getContentAsString().contains("El email no puede estar vacio"))
        );
    }

    @Test
    void emptyEmailUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("") // Empty email
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
                () -> assertTrue(result.getResponse().getContentAsString().contains("El email no puede estar vacio")),
                () -> assertFalse(result.getResponse().getContentAsString().contains("El email debe ser valido"))
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
                () -> assertTrue(responseContent.contains("El telefono debe tener 9 numeros")),
                () -> assertFalse(responseContent.contains("El telefono no puede estar vacio"))
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
                () -> assertTrue(responseContent.contains("El telefono no puede estar vacio")),
                () -> assertFalse(responseContent.contains("El telefono debe tener 9 numeros"))
        );
    }

    @Test
    void emptyFotoPerfilUpdate() throws Exception {
        ClienteRequestUpdate clienteRequestUpdate = ClienteRequestUpdate.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .fotoPerfil("") // Empty fotoPerfil
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
                .fotoDni("") // Empty fotoDni
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
    void DeleteCliente() throws Exception {
        Mockito.doNothing().when(clienteService).deleteById("unique-guid");

        mockMvc.perform(delete("/v1/cliente/unique-guid"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "userName", roles = {"USER"})
    void me() throws Exception {
        String userGuid = "1111aaaa";

        User user = User.builder()
                .id(1L)
                .guid(userGuid)
                .username("userName")
                .password("$2a$10$to0IqpINy9GXDo4IH9SKIOOT0cU5kg692jLdV0aPzR/rF3cUt97Fy")
                .build();

        System.out.println("User for test: " + user);

        ClienteResponse mockResponse = ClienteResponse.builder()
                .guid(user.getGuid())
                .dni("12345678A")
                .nombre("John")
                .apellidos("Doe")
                .email("john.doe@example.com")
                .telefono("123456789")
                .build();

        Mockito.when(clienteService.getUserByGuid(userGuid)).thenReturn(mockResponse);

        mockMvc.perform(get(myEndpoint + "/me/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(userGuid))
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("John"))
                .andExpect(jsonPath("$.apellidos").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"));

        Mockito.verify(clienteService).getUserByGuid(userGuid);
    }

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
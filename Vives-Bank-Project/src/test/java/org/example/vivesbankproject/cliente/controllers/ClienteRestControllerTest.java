package org.example.vivesbankproject.cliente.controllers;

import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
@SpringBootTest
@AutoConfigureMockMvc
class ClienteRestControllerTest {

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
                .cuentas(Set.of(CuentaResponse.builder().guid("cuenta1-guid").build()))
                .user(UserResponse.builder().guid("user-guid").username("testuser").build())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Page<ClienteResponse> page = new PageImpl<>(List.of(clienteResponse), PageRequest.of(0, 10), 1);
        Mockito.when(clienteService.getAll(any(), any(), any(), any(), any(), any())).thenReturn(page);
        Mockito.when(paginationLinksUtils.createLinkHeader(eq(page), any())).thenReturn("");

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
                .andExpect(jsonPath("$.content[0].cuentas[0].guid").value("cuenta1-guid"))
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

        Mockito.when(clienteService.getById("unique-guid")).thenReturn(clienteResponse);

        mockMvc.perform(get("/v1/cliente/unique-guid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"));
    }




    @Test
    void getProductos() throws Exception {
        ClienteResponseProductos clienteResponseProductos = ClienteResponseProductos.builder()
                .guid("unique-guid")
                .nombre("Juan")
                .cuentas(Set.of(CuentaResponse.builder().guid("cuenta1-guid").build()))
                .build();

        Mockito.when(clienteService.getProductos("unique-guid")).thenReturn(clienteResponseProductos);

        mockMvc.perform(get("/v1/cliente/unique-guid/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value("unique-guid"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.cuentas").isArray())
                .andExpect(jsonPath("$.cuentas[0].guid").value("cuenta1-guid"));
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

        Mockito.when(clienteService.save(any(ClienteRequestSave.class))).thenReturn(clienteResponse);

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

        Mockito.when(clienteService.update(eq("unique-guid"), any(ClienteRequestUpdate.class))).thenReturn(clienteResponse);

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
    void AddCuentas() throws Exception {
        ClienteCuentasRequest clienteCuentasRequest = ClienteCuentasRequest.builder()
                .cuentasIds(Set.of("cuenta1-guid", "cuenta2-guid"))
                .build();

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        Mockito.when(clienteService.addCuentas(eq("unique-guid"), any(ClienteCuentasRequest.class))).thenReturn(clienteResponse);

        mockMvc.perform(put("/v1/cliente/unique-guid/add")
                        .contentType("application/json")
                        .content("{ \"cuentasIds\": [\"cuenta1-guid\", \"cuenta2-guid\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"));
    }

    @Test
    void DeleteCuentas() throws Exception {
        ClienteCuentasRequest clienteCuentasRequest = ClienteCuentasRequest.builder()
                .cuentasIds(Set.of("cuenta1-guid", "cuenta2-guid"))
                .build();

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .guid("unique-guid")
                .dni("12345678A")
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@example.com")
                .telefono("123456789")
                .build();

        Mockito.when(clienteService.deleteCuentas(eq("unique-guid"), any(ClienteCuentasRequest.class))).thenReturn(clienteResponse);

        mockMvc.perform(put("/v1/cliente/unique-guid/delete")
                        .contentType("application/json")
                        .content("{ \"cuentasIds\": [\"cuenta1-guid\", \"cuenta2-guid\"] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678A"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.telefono").value("123456789"));
    }

    @Test
    void DeleteCliente() throws Exception {
        Mockito.doNothing().when(clienteService).deleteById("unique-guid");

        mockMvc.perform(delete("/v1/cliente/unique-guid"))
                .andExpect(status().isNoContent());
    }

   /* @Test
    void testValidationExceptionHandler() throws Exception {
        mvc.perform(post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nombre\": \"\" }")) // En este caso, el campo 'nombre' está vacío, lo que debería causar una excepción de validación
                .andExpect(status().isBadRequest()) // El estado debe ser 400 Bad Request
                .andExpect(jsonPath("$.nombre").value("El nombre no puede ser un campo vacio"))
                .andReturn();
    }*/
}
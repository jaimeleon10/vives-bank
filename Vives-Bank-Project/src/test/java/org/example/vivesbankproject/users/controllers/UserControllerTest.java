package org.example.vivesbankproject.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.services.UserService;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "adminPassword123", roles = {"ADMIN", "USER"})
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String myEndpoint = "/v1/usuario";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PaginationLinksUtils paginationLinksUtils;

    @Test
    void GetAll() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .guid("unique-guid")
                .username("johndoe")
                .password("password123")
                .roles(Set.of(Role.USER))
                .createdAt("2024-11-26T15:23:45.123")
                .updatedAt("2024-11-27T10:15:30.456")
                .isDeleted(false)
                .build();

        Page<UserResponse> page = new PageImpl<>(List.of(userResponse));

        when(userService.getAll(any(), any(), any())).thenReturn(page);
        when(paginationLinksUtils.createLinkHeader(eq(page), any())).thenReturn("");

        mockMvc.perform(get("/v1/usuario")
                        .param("username", "johndoe")
                        .param("roles", "USER")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].guid").value("unique-guid"))
                .andExpect(jsonPath("$.content[0].username").value("johndoe"))
                .andExpect(jsonPath("$.content[0].password").value("password123"))
                .andExpect(jsonPath("$.content[0].roles[0]").value("USER"))
                .andExpect(jsonPath("$.content[0].createdAt").value("2024-11-26T15:23:45.123"))
                .andExpect(jsonPath("$.content[0].updatedAt").value("2024-11-27T10:15:30.456"))
                .andExpect(jsonPath("$.content[0].isDeleted").value(false));
    }
    @Test
    void GetById() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .guid("unique-guid")
                .username("johndoe")
                .password("password123")
                .roles(Set.of(Role.USER))
                .build();


        when(userService.getById("unique-guid")).thenReturn(userResponse);

        mockMvc.perform(get("/v1/cuentas/unique-guid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.password").value("password123"))
                .andExpect(jsonPath("$.roles").value("USER"));

    }


    @Test
    void Save() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .guid("unique-guid")
                .username("johndoe")
                .password("password123")
                .roles(Set.of(Role.USER))
                .build();

        when(userService.save(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/usuario")
                        .contentType("application/json")
                        .content("{ \"username\": \"johndoe\", \"password\": \"password123\", \"roles\": \"USER\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.password").value("password123"))
                .andExpect(jsonPath("$.roles").value("USER"));
    }


    @Test
    void Update() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .username("johndoe")
                .password("password123")
                .roles(Set.of(Role.USER))
                .build();

        UserResponse userResponse = UserResponse.builder()
                .guid("unique-guid")
                .username("johndoe")
                .password("password123")
                .roles(Set.of(Role.USER))
                .build();


        when(userService.update(eq("unique-guid"), any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/usuario/unique-guid")
                        .contentType("application/json")
                        .content("{ \"username\": \"johndoe\", \"password\": \"password123\", \"roles\": \"USER\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.password").value("password123"))
                .andExpect(jsonPath("$.roles").value("USER"));
    }

    @Test
    void Delete() throws Exception {
        Mockito.doNothing().when(userService).deleteById("unique-guid");

        mockMvc.perform(patch("/v1/usuario/unique-guid"))
                .andExpect(status().isNoContent());
    }
    @Test
    void handleValidationExceptionUpdateError() throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.put("/v1/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username no puede estar vacio"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println(responseContent);

        assertAll(
                () -> assertTrue(responseContent.contains("\"username\":\"Username no puede estar vacio\""))
        );
    }
}
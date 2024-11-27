package org.example.vivesbankproject.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.services.UserService;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private PaginationLinksUtils paginationLinksUtils;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        objectMapper = new ObjectMapper();

        // Configuración del mock para devolver un valor no nulo
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);

        UserRequest userRequest = new UserRequest("testUser", "password", roles, false);
        UserResponse userResponse = new UserResponse("123", "testUser", "password", roles, "2024-11-27T00:00:00", "2024-11-27T00:00:00", false);

        // Mock de la respuesta del servicio
        when(userService.save(any(UserRequest.class))).thenReturn(userResponse);
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void GetAllPageable() throws Exception {
        when(userService.getAll(any(), any(), any())).thenReturn(mockPageResult());

        mockMvc.perform(get("/api/v1/usuario")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0].username").value("testUser"))
                .andExpect(header().exists("link"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void GetById() throws Exception {
        when(userService.getById("123")).thenReturn(mockUserResponse());

        mockMvc.perform(get("/api/v1/usuario/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void CreateUser() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);

        UserRequest userRequest = new UserRequest("testUser", "password", roles, false);
        UserResponse userResponse = new UserResponse("123", "testUser", "password", roles, "2024-11-27T00:00:00", "2024-11-27T00:00:00", false);

        when(userService.save(any())).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void UpdateUser() throws Exception {
        String userId = "123";

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);

        UserRequest userRequest = new UserRequest("updatedUser", "newPassword", roles, false);
        UserResponse userResponse = new UserResponse(userId, "updatedUser", "newPassword", roles, "2024-11-27T00:00:00", "2024-11-27T00:00:00", false);

        when(userService.update(eq(userId), any())).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/usuario/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void DeleteUser() throws Exception {
        String userId = "123";

        doNothing().when(userService).deleteById(userId);

        mockMvc.perform(delete("/api/v1/usuario/{id}", userId))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    private Page<UserResponse> mockPageResult() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);

        List<UserResponse> users = Arrays.asList(new UserResponse("123", "testUser", "password", roles, "2024-11-27T00:00:00", "2024-11-27T00:00:00", false));
        return new PageImpl<>(users);
    }

    private UserResponse mockUserResponse() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);

        return new UserResponse("123", "testUser", "password", roles, "2024-11-27T00:00:00", "2024-11-27T00:00:00", false);
    }
}
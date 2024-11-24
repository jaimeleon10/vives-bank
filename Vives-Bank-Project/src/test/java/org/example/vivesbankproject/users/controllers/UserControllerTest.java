package org.example.vivesbankproject.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.services.UserService;
import org.example.vivesbankproject.utils.PageResponse;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PaginationLinksUtils paginationLinksUtils;

    private final String myEndpoint = "/v1/usuario";
    private UserResponse userResponseTest;
    private UserRequest userRequestTest;

    @BeforeEach
    void setUp() {
        userRequestTest = UserRequest.builder()
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .isDeleted(false)
                .build();

        userResponseTest = UserResponse.builder()
                .guid("unique-guid")
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();
    }

    @Test
    void getAllPageable() throws Exception {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<UserResponse> userPage = new PageImpl<>(List.of(userResponseTest));

        when(userService.getAll(
                Optional.of(userResponseTest.getUsername()),
                Optional.of(Role.USER),
                pageRequest
        )).thenReturn(userPage);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .param("username", "testuser")
                                .param("roles", "USER")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<User> pageResponse = objectMapper.readValue(
                response.getContentAsString(),
                objectMapper.getTypeFactory().constructParametricType(PageResponse.class, User.class)
        );

        List<User> res = pageResponse.content();

        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> assertFalse(res.isEmpty()),
                () -> assertTrue(res.stream().anyMatch(r -> r.getGuid() != null && r.getGuid().equals(userResponseTest.getGuid()))),
                () -> assertEquals(res.size(), 1),
                () -> assertTrue(res.get(0).getGuid().equals(userResponseTest.getGuid()))
        );

        verify(userService, times(1)).getAll(
                Optional.of(userResponseTest.getUsername()),
                Optional.of(Role.USER),
                pageRequest
        );
    }

    @Test
    void getById() throws Exception {
        when(userService.getById("unique-guid")).thenReturn(userResponseTest);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/unique-guid")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        UserResponse res = objectMapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(userResponseTest.getGuid(), res.getGuid()),
                () -> assertEquals(userResponseTest.getUsername(), res.getUsername()),
                () -> assertEquals(userResponseTest.getPassword(), res.getPassword()),
                () -> assertEquals(userResponseTest.getRoles(), res.getRoles())
        );

        verify(userService, times(1)).getById("unique-guid");
    }

    @Test
    void createUser() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .username("testuser2")
                .password("password")
                .roles(Set.of(Role.USER))
                .isDeleted(false)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .guid("unique-guid")
                .username("testuser2")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        when(userService.save(userRequest)).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        UserResponse res = objectMapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(userResponse.getGuid(), res.getGuid()),
                () -> assertEquals(userResponse.getUsername(), res.getUsername()),
                () -> assertEquals(userResponse.getPassword(), res.getPassword()),
                () -> assertEquals(userResponse.getRoles(), res.getRoles())
        );

        verify(userService, times(1)).save(userRequest);
    }

    @Test
    void usernameBlank() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .username("")
                .password("password")
                .roles(Set.of(Role.USER))
                .isDeleted(false)
                .build();

        MvcResult response = mockMvc.perform(
                        post("/v1/usuario")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = response.getResponse().getContentAsString();
        System.out.println(responseBody);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getResponse().getStatus()),
                () -> assertTrue(responseBody.contains("Username no puede estar vacio"))
        );
    }

    @Test
    void updateUser() throws Exception {
        User user = new User();
        user.setGuid("unique-guid");
        user.setUsername("testuser2");
        user.setPassword("password2");
        user.setRoles(Set.of(Role.USER));
        user.setIsDeleted(false);

        UserRequest userRequest = UserRequest.builder()
                .username("testuser2")
                .password("password2")
                .roles(Set.of(Role.USER))
                .isDeleted(false)
                .build();

        UserResponse userResponse = UserResponse.builder()
                .guid("unique-guid")
                .username("testuser2")
                .password("password2")
                .roles(Set.of(Role.USER))
                .build();

        when(userService.update(user.getGuid(), userRequest)).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/unique-guid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        String responseBody = response.getContentAsString();
        assertNotNull(responseBody);

        UserResponse res = objectMapper.readValue(responseBody, UserResponse.class);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(user.getGuid(), res.getGuid()),
                () -> assertEquals(user.getUsername(), res.getUsername()),
                () -> assertEquals(userRequest.getPassword(), res.getPassword()),
                () -> assertEquals(user.getRoles(), res.getRoles())
        );

        verify(userService, times(1)).update(user.getGuid(), userRequest);
    }


    @Test
    void deleteUser() throws Exception {
        String userId = "6c257ab6-e588-4cef-a479-c2f8fcd7379a";

        doNothing().when(userService).deleteById(userId);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.delete(myEndpoint + "/" + userId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus(), "El estado debe ser 204 No Content"),
                () -> assertEquals("", response.getContentAsString(), "El cuerpo de la respuesta debe estar vacío")
        );

        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    void handleValidationExceptions() throws Exception {
        mockMvc.perform(post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Username no puede estar vacio"))
                .andReturn();
    }
}
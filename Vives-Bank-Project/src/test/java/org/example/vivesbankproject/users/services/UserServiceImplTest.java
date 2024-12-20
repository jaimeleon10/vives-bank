package org.example.vivesbankproject.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.vivesbankproject.rest.users.dto.UserRequest;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.example.vivesbankproject.rest.users.exceptions.UserExists;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.rest.users.mappers.UserMapper;
import org.example.vivesbankproject.rest.users.models.Role;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
import org.example.vivesbankproject.rest.users.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .guid("hola")
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        userRequest = UserRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        userResponse = UserResponse.builder()
                .guid(user.getGuid())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(usersPage);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAll(Optional.of("testuser"), Optional.of(String.valueOf(Role.USER)), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void getById() {
        when(userRepository.findByGuid(user.getGuid())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getById(user.getGuid());

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getByIdNotFound() {
        String id = "adios";

        when(userRepository.findByGuid(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundById.class, () -> userService.getById(id));

        assertEquals("Usuario con id 'adios' no encontrado", exception.getMessage());
    }

    @Test
    void getByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getByUsernameNotFound() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundByUsername.class, () -> userService.getByUsername("unknownuser"));

        assertEquals("Usuario con username 'unknownuser' no encontrado", exception.getMessage());
    }

    @Test
    void save() {
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.empty());
        when(userMapper.toUser(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.save(userRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void saveUserExists() {
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.of(user));

        UserExists thrown = assertThrows(UserExists.class, () -> userService.save(userRequest));

        assertEquals("El nombre de usuario 'testuser' ya existe", thrown.getMessage());
    }

    @Test
    void update() {
        String id = "hola";
        String username = "testusuario";

        User user = new User();
        user.setGuid(id);
        user.setUsername(username);

        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testusuarioUpdate");

        when(userRepository.findByGuid(id)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("testusuarioUpdate")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserUpdate(userRequest, user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.update(id, userRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void updateUserNotFound() {
        String id = "UUID.randomUUID()";

        when(userRepository.findByGuid(id)).thenReturn(Optional.empty());

        UserNotFoundById thrown = assertThrows(UserNotFoundById.class, () -> userService.update(id, userRequest));

        assertEquals("Usuario con id 'UUID.randomUUID()' no encontrado", thrown.getMessage());
    }

    @Test
    void updateUserExist() {
        String id = "hola";

        when(userRepository.findByGuid(id)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.of(user));

        UserExists thrown = assertThrows(UserExists.class, () -> userService.update(id, userRequest));

        assertEquals("El nombre de usuario 'testuser' ya existe", thrown.getMessage());
    }

    @Test
    void deleteById() {
        String id = "buenasnoches";
        User user = User.builder()
                .guid("buenasnoches")
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        when(userRepository.findByGuid(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deleteById(id);

        verify(userRepository, times(1)).findByGuid(id);
    }

    @Test
    void deleteByIdUserNotFound() {
        String id = "UUID.randomUUID()";

        when(userRepository.findByGuid(id)).thenReturn(Optional.empty());

        UserNotFoundById thrown = assertThrows(UserNotFoundById.class, () -> userService.deleteById(id));

        assertEquals("Usuario con id 'UUID.randomUUID()' no encontrado", thrown.getMessage());
    }
}
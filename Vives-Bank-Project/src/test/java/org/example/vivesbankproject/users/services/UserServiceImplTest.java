package org.example.vivesbankproject.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.exceptions.UserExists;
import org.example.vivesbankproject.users.exceptions.UserNotFound;
import org.example.vivesbankproject.users.mappers.UserMapper;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
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
import org.springframework.http.HttpStatus;

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
                .id(UUID.randomUUID())
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        userRequest = UserRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    @Test
    void GetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> usersPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(usersPage);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        Page<UserResponse> result = userService.getAll(Optional.of("testuser"), Optional.of(Role.USER), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
    }

    @Test
    void GetById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void GetByIdNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFound.class, () -> userService.getById(id));

        assertEquals("User with ID " + id + " not found", exception.getMessage());
    }

    @Test
    void GetByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void GetByUsernameNotFound() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFound.class, () -> userService.getByUsername("unknownuser"));

        assertEquals("User with username unknownuser not found", exception.getMessage());
    }

    @Test
    void Save() {
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.empty());
        when(userMapper.toUser(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.save(userRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void SaveUserNotFound() {
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.empty());

       UserNotFound thrown = assertThrows(UserNotFound.class, () -> userService.save(userRequest));

        assertEquals("El usuario 'testuser' no existe", thrown.getMessage());
    }

//Para comprobar y testear la excepcion UserExists
    @Test
    void SaveUserExists() {
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.of(user));

        UserExists thrown = assertThrows(UserExists.class, () -> userService.save(userRequest));

        assertEquals("El nombre de usuario 'testuser' ya existe", thrown.getMessage());
    }


    @Test
    void Update() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.empty());
        when(userMapper.toUser(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.update(id, userRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void UpdateUserNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFound thrown = assertThrows(UserNotFound.class, () -> userService.update(id, userRequest));

        assertEquals("El usuario 'testuser' no existe", thrown.getMessage());
    }

    //Para comprobar y testear la excepcion UserExists
    @Test
    void UpdateUserExist() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(userRequest.getUsername())).thenReturn(Optional.of(user));

       UserExists thrown = assertThrows(UserExists.class, () -> userService.update(id, userRequest));

        assertEquals("El nombre de usuario 'testuser' ya existe", thrown.getMessage());
    }

    @Test
    void DeleteById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteById(user.getId()));

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void DeleteByIdUserNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFound thrown = assertThrows(UserNotFound.class, () -> userService.deleteById(id));

        assertEquals("El usuario 'testuser' no existe", thrown.getMessage());
    }
}

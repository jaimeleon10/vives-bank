package org.example.vivesbankproject.users.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void ToUserResponse() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("password")
                .roles(new HashSet<>(Arrays.asList(Role.USER)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserResponse userResponse = userMapper.toUserResponse(user);

        assertNotNull(userResponse);
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getUsername(), userResponse.getUsername());
        assertEquals(user.getPassword(), userResponse.getPassword());
        assertEquals(user.getRoles(), userResponse.getRoles());
    }

    @Test
    void ToUser() {
        UserRequest userRequest = UserRequest.builder()
                .username("testuser")
                .password("password")
                .build();

        User user = userMapper.toUser(userRequest);

        assertNotNull(user);
        assertEquals(userRequest.getUsername(), user.getUsername());
        assertEquals(userRequest.getPassword(), user.getPassword());
    }
}

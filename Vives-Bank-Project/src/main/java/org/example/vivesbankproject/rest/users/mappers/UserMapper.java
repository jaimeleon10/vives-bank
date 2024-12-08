package org.example.vivesbankproject.rest.users.mappers;

import org.example.vivesbankproject.rest.users.dto.UserRequest;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.example.vivesbankproject.rest.users.models.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .guid(user.getGuid())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt().toString())
                .updatedAt(user.getUpdatedAt().toString())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    public User toUser(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }

    public User toUserUpdate(UserRequest userRequest, User user) {
        return User.builder()
                .id(user.getId())
                .guid(user.getGuid())
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .roles(userRequest.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(userRequest.getIsDeleted())
                .build();
    }
}

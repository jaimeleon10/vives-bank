package org.example.vivesbankproject.users.mappers;

import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .guid(user.getGuid())
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    public User toUser(UserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }
}

package org.example.vivesbankproject.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.users.models.Role;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    private String guid;
    private String username;
    private String password;
    private Set<Role> roles;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
}

package org.example.vivesbankproject.users.services;

import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.exceptions.UserExists;
import org.example.vivesbankproject.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.users.exceptions.UserNotFoundByUsername;
import org.example.vivesbankproject.users.mappers.UserMapper;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"usuario"})
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserResponse> getAll(Optional<String> username, Optional<Role> roles, Pageable pageable) {
        Specification<User> specUsername = (root, query, criteriaBuilder) ->
                username.map(u -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + u.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specRole = (root, query, criteriaBuilder) ->
                roles.map(r -> criteriaBuilder.equal(root.get("roles"), r))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> criterio = Specification.where(specUsername).and(specRole);

        Page<User> userPage = userRepository.findAll(criterio, pageable);

        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    @Cacheable(key = "#id")
    public UserResponse getById(String id) {
        var user = userRepository.findByGuid(id).orElseThrow(() -> new UserNotFoundById(id));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Cacheable(key = "#username")
    public UserResponse getByUsername(String username) {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundByUsername(username));
        return userMapper.toUserResponse(user);
    }

    @Override
    @CachePut(key = "#result.guid")
    public UserResponse save(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserExists(userRequest.getUsername());
        }
        var user = userRepository.save(userMapper.toUser(userRequest));
        return userMapper.toUserResponse(user);
    }

    @Override
    @CachePut(key = "#result.guid")
    public UserResponse update(String id, UserRequest userRequest) {
        var user = userRepository.findByGuid(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserExists(userRequest.getUsername());
        }
        var userUpdated = userRepository.save(userMapper.toUserUpdate(userRequest, user));
        return userMapper.toUserResponse(userUpdated);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(String id) {
        var user = userRepository.findByGuid(id).orElseThrow(
                () -> new UserNotFoundById(id)
        );
        user.setIsDeleted(true);
        userRepository.save(user);
    }
}

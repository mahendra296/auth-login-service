package com.auth.service;

import com.auth.dto.LoginRequest;
import com.auth.dto.UserDto;
import com.auth.dto.UserResponse;
import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.mapper.UserMapper;
import com.auth.model.User;
import com.auth.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest loginRequest) {
        User user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseList(users);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }
}

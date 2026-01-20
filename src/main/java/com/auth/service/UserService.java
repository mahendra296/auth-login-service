package com.auth.service;

import com.auth.dto.LoginRequest;
import com.auth.dto.UserDto;
import com.auth.dto.UserResponse;
import com.auth.enums.AuthProvider;
import com.auth.exception.InvalidCredentialsException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.mapper.UserMapper;
import com.auth.model.AuthAccount;
import com.auth.model.User;
import com.auth.repository.AuthAccountRepository;
import com.auth.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
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

    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<AuthAccount> findAuthAccount(AuthProvider provider, String providerAccountId) {
        return authAccountRepository.findByProviderAndProviderAccountId(provider, providerAccountId);
    }

    @Transactional
    public AuthAccount createOAuthAccount(User user, AuthProvider provider, String providerAccountId) {
        AuthAccount authAccount = AuthAccount.builder()
                .user(user)
                .provider(provider)
                .providerAccountId(providerAccountId)
                .build();
        return authAccountRepository.save(authAccount);
    }

    @Transactional
    public UserResponse createUserWithOAuth(
            String email, String firstName, String lastName, AuthProvider provider, String providerAccountId) {
        User user = User.builder()
                .email(email)
                .firstName(firstName != null ? firstName : "")
                .lastName(lastName != null ? lastName : "")
                .password(generateRandomPassword(8)) // Random password for OAuth users
                .build();

        User savedUser = userRepository.save(user);

        AuthAccount authAccount = AuthAccount.builder()
                .user(savedUser)
                .provider(provider)
                .providerAccountId(providerAccountId)
                .build();

        authAccountRepository.save(authAccount);

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse processOAuthUser(
            String email, String firstName, String lastName, AuthProvider provider, String providerAccountId) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            Optional<AuthAccount> existingAuthAccount =
                    authAccountRepository.findByProviderAndProviderAccountId(provider, providerAccountId);

            if (existingAuthAccount.isEmpty()) {
                createOAuthAccount(user, provider, providerAccountId);
            }

            return userMapper.toResponse(user);
        }

        return createUserWithOAuth(email, firstName, lastName, provider, providerAccountId);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}

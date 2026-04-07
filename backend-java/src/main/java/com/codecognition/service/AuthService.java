package com.codecognition.service;

import com.codecognition.model.AuthResponse;
import com.codecognition.model.LoginRequest;
import com.codecognition.model.RegisterRequest;
import com.codecognition.model.User;
import com.codecognition.repository.UserRepository;
import com.codecognition.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.email);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + request.email);
        }

        User foundUser = user.get();

        if (!foundUser.getIsActive()) {
            throw new IllegalArgumentException("User account is inactive");
        }

        if (!passwordEncoder.matches(request.password, foundUser.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(
                foundUser.getId().toString(),
                foundUser.getEmail(),
                foundUser.getRole().toString()
        );

        return new AuthResponse(
                token,
                foundUser.getEmail(),
                foundUser.getFullName(),
                foundUser.getRole().toString(),
                foundUser.getId()
        );
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.email).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + request.email);
        }

        // Create new user
        User newUser = new User();
        newUser.setEmail(request.email);
        newUser.setFullName(request.fullName);
        newUser.setPassword(passwordEncoder.encode(request.password));
        newUser.setRole(User.UserRole.USER);
        newUser.setIsActive(true);

        User savedUser = userRepository.save(newUser);

        String token = jwtTokenProvider.generateToken(
                savedUser.getId().toString(),
                savedUser.getEmail(),
                savedUser.getRole().toString()
        );

        return new AuthResponse(
                token,
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().toString(),
                savedUser.getId()
        );
    }
}

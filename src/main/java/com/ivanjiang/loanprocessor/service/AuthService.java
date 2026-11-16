package com.ivanjiang.loanprocessor.service;

import com.ivanjiang.loanprocessor.dto.AuthResponse;
import com.ivanjiang.loanprocessor.dto.LoginRequest;
import com.ivanjiang.loanprocessor.dto.RegisterRequest;
import com.ivanjiang.loanprocessor.model.User;
import com.ivanjiang.loanprocessor.repository.UserRepository;
import com.ivanjiang.loanprocessor.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

        String token = tokenProvider.generateTokenFromUsername(request.getUsername());
        return new AuthResponse(token, request.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token, request.getUsername());
    }
}

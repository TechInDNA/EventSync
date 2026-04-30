package com.techindna.eventsync.service;

import com.techindna.eventsync.config.TokenProvider;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.repository.AuthRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository, TokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public Administrator emailLogin(String email, String password) {
        Administrator admin = authRepository.findAdminByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return admin;
    }

    public String generateToken(Administrator admin) {
        return tokenProvider.generateAccessToken(admin);
    }
}

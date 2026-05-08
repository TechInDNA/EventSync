package com.techindna.eventsync.service;

import com.techindna.eventsync.config.TokenProvider;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.exception.TooManyRequestException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.repository.AuthRepository;
import com.techindna.eventsync.validator.StringValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final StringValidator stringValidator;
    private static final int MAX_ATTEMPT_LIMIT = 5;
    private static final Duration RESET_DURATION = Duration.ofHours(12);
    private int loggingAttempt = 0;
    private Instant firstFailureTime = null;

    public AuthService(AuthRepository authRepository, TokenProvider tokenProvider, PasswordEncoder passwordEncoder, StringValidator stringValidator) {
        this.authRepository = authRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.stringValidator = stringValidator;
    }

    public Administrator emailLogin(String email, String password) {
        stringValidator.checkNullData("email", email);
        stringValidator.checkNullData("password", password);
        stringValidator.ValidateEmail(email);

        if (firstFailureTime != null && Duration.between(firstFailureTime, Instant.now()).compareTo(RESET_DURATION) >= 0) {
            loggingAttempt = 0;
            firstFailureTime = null;
        }

        if (loggingAttempt >= MAX_ATTEMPT_LIMIT) {
            throw new TooManyRequestException("Too many login failures. Try again in 12 hours.");
        }

        Administrator admin = authRepository.getAdminByEmail(email);

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            loggingAttempt++;
            if (loggingAttempt == 1) {
                firstFailureTime = Instant.now();
            }
            throw new UnauthorizedException("Invalid credentials.");
        }

        return admin;
    }

    public String generateToken(Administrator admin) {
        return tokenProvider.generateAccessToken(admin);
    }
}

package com.techindna.eventsync.service;

import com.techindna.eventsync.config.TokenProvider;
import com.techindna.eventsync.dto.AuthParticipantRequestDto;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.entity.Participant;
import com.techindna.eventsync.exception.TooManyRequestException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.repository.AuthRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final DataValidator dataValidator;
    private static final int MAX_ATTEMPT_LIMIT = 5;
    private static final Duration RESET_DURATION = Duration.ofHours(12);
    private int loggingAttempt = 0;
    private Instant firstFailureTime = null;

    public AuthService(AuthRepository authRepository, TokenProvider tokenProvider, PasswordEncoder passwordEncoder, DataValidator dataValidator) {
        this.authRepository = authRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.dataValidator = dataValidator;
    }

    @Transactional(readOnly = true)
    public Administrator logInByEmailAndPassword(String email, String password) {
        dataValidator.validateEmail(email);
        dataValidator.checkNullData("password", password);

        if (firstFailureTime != null && Duration.between(firstFailureTime, Instant.now()).compareTo(RESET_DURATION) >= 0) {
            loggingAttempt = 0;
            firstFailureTime = null;
        }

        if (loggingAttempt >= MAX_ATTEMPT_LIMIT) {
            throw new TooManyRequestException("Too many login failures. Try again in 12 hours.");
        }

        Administrator admin = authRepository.findAdminDataByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));

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

    public Participant identifyOrRegisterParticipant(AuthParticipantRequestDto request) {
        dataValidator.validateParticipantData(request);

        Optional<Participant> participant = authRepository.findParticipant(request.getEmail(), request.getFirstName(), request.getLastName());
        return participant.orElseGet(() -> authRepository
                .saveParticipant(request.getFirstName(), request.getLastName(), request.getEmail()));

    }

    public String generateParticipantToken(Participant participant) {
        return tokenProvider.generateAccessToken(participant);
    }
}

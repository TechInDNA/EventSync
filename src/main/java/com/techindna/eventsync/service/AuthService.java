package com.techindna.eventsync.service;

import com.techindna.eventsync.config.TokenProvider;
import com.techindna.eventsync.dto.auth.AuthParticipantRequestDto;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.entity.Participant;
import com.techindna.eventsync.exception.TooManyRequestException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.repository.AuthRepository;
import com.techindna.eventsync.validator.DataValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final DataValidator dataValidator;
    private static final int MAX_ATTEMPT_LIMIT = 5;

    public AuthService(AuthRepository authRepository, TokenProvider tokenProvider, PasswordEncoder passwordEncoder, DataValidator dataValidator) {
        this.authRepository = authRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.dataValidator = dataValidator;
    }

    @Transactional(noRollbackFor = {UnauthorizedException.class, TooManyRequestException.class})
    public Administrator logInByEmailAndPassword(String email, String password, String ipAddress) {
        dataValidator.validateEmail(email);
        dataValidator.checkNullData("password", password);

        checkClient(ipAddress);
        Administrator admin = authRepository.findAdminDataByEmail(email).orElse(null);

        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            int attempts = authRepository.incrementFailedAttempt(ipAddress);
            if (attempts >= MAX_ATTEMPT_LIMIT) {
                throw new TooManyRequestException("You are blocked due to too many failed login attempts.");
            }
            throw new UnauthorizedException(
                    String.format("Invalid credentials, %d attempts left.", MAX_ATTEMPT_LIMIT - attempts)
            );
        }

        authRepository.deleteBlacklistedIp(ipAddress);
        return admin;
    }

    @Transactional(readOnly = true)
    public void checkClient(String ipAddress) {
        Optional<Integer> blacklisted = authRepository.findBlacklistedIp(ipAddress);
        if (blacklisted.isPresent() && blacklisted.get() >= MAX_ATTEMPT_LIMIT) {
            throw new UnauthorizedException("You are not authorized to access this resource.");
        }
    }

    public String generateToken(Administrator admin) {
        return tokenProvider.generateAccessToken(admin);
    }

    @Transactional
    public Participant identifyOrRegisterParticipant(AuthParticipantRequestDto request, String ipAddress) {
        checkClient(ipAddress);
        dataValidator.validateParticipantData(request);

        Optional<Participant> participant = authRepository.findParticipant(request.getEmail(), request.getFirstName(), request.getLastName());
        return participant.orElseGet(() -> authRepository
                .saveParticipant(request.getFirstName(), request.getLastName(), request.getEmail()));

    }

    public String generateParticipantToken(Participant participant) {
        return tokenProvider.generateAccessToken(participant);
    }
}

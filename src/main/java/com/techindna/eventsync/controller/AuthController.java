package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.*;
import com.techindna.eventsync.dto.auth.AuthLoginRequestDto;
import com.techindna.eventsync.dto.auth.AuthLoginResponseDto;
import com.techindna.eventsync.dto.auth.AuthParticipantRequestDto;
import com.techindna.eventsync.dto.auth.AuthParticipantResponseDto;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.entity.Participant;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.TooManyRequestException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.mapper.AuthMapper;
import com.techindna.eventsync.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private static final int COOKIE_MAX_AGE = 43200;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDto request, HttpServletRequest servletRequest) {
        try{
            String ipAddress = servletRequest.getRemoteAddr();
            Administrator admin = authService.logInByEmailAndPassword(request.getEmail(), request.getPassword(), ipAddress);
            String token = authService.generateToken(admin);

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(COOKIE_MAX_AGE)
                    .sameSite("Strict")
                    .build();

            AuthLoginResponseDto response = new AuthLoginResponseDto();
            UserResponseDto userDto = AuthMapper.toUserResponseDto(admin);
            response.setUser(userDto);
            response.setToken(token);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(response);
        } catch (TooManyRequestException e){
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(e.getMessage());
        }
        catch (BadRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
        catch (UnauthorizedException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
        catch (InternalServerErrorException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }

    @PostMapping("/participant")
    public ResponseEntity<?> identifyOrRegisterParticipant(@RequestBody AuthParticipantRequestDto request) {
        try {
            Participant participant = authService.identifyOrRegisterParticipant(request);

            String token = authService.generateParticipantToken(participant);

            ParticipantDto p = AuthMapper.toParticipantDto(participant);

            AuthParticipantResponseDto response = new AuthParticipantResponseDto();
            response.setToken(token);
            response.setParticipant(p);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred, please try again later");
        }
    }
}

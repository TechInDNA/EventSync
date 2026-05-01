package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.AuthLoginRequestDto;
import com.techindna.eventsync.dto.AuthLoginResponseDto;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.service.AuthService;
import com.techindna.eventsync.validator.AuthValidator;
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
    private final AuthValidator authValidator;

    public AuthController(AuthService authService, AuthValidator authValidator) {
        this.authService = authService;
        this.authValidator = authValidator;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDto request) {
        try{
            authValidator.ValidateData(request.getEmail(), request.getPassword());
            Administrator admin = authService.emailLogin(request.getEmail(), request.getPassword());
            String token = authService.generateToken(admin);

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(86400)
                    .sameSite("Strict")
                    .build();

            AuthLoginResponseDto response = new AuthLoginResponseDto();
            response.setUser(admin);
            response.setToken(token);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(response);
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
                    .body(e.getMessage());
        }
    }
}

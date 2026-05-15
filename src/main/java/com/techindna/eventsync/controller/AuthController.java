package com.techindna.eventsync.controller;

import com.techindna.eventsync.dto.AuthLoginRequestDto;
import com.techindna.eventsync.dto.AuthLoginResponseDto;
import com.techindna.eventsync.dto.UserResponseDto;
import com.techindna.eventsync.entity.Administrator;
import com.techindna.eventsync.exception.BadRequestException;
import com.techindna.eventsync.exception.InternalServerErrorException;
import com.techindna.eventsync.exception.TooManyRequestException;
import com.techindna.eventsync.exception.UnauthorizedException;
import com.techindna.eventsync.service.AuthService;
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
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDto request) {
        try{
            Administrator admin = authService.logInAdmin(request.getEmail(), request.getPassword())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));
            String token = authService.generateToken(admin);

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(COOKIE_MAX_AGE)
                    .sameSite("Strict")
                    .build();

            AuthLoginResponseDto response = new AuthLoginResponseDto();
            UserResponseDto userDto = new UserResponseDto();
            userDto.setId(admin.getId());
            userDto.setFirstName(admin.getFirstName());
            userDto.setLastName(admin.getLastName());
            userDto.setEmail(admin.getEmail());
            userDto.setRole(admin.getRole());
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
}
